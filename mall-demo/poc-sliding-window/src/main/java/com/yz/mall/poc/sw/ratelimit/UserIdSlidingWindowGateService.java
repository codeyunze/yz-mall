package com.yz.mall.poc.sw.ratelimit;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

/**
 * 滑动窗口 UserId 种类校验，供 {@link com.yz.mall.poc.sw.web.UserIdSlidingWindowGuardFilter} 调用。
 * <p>
 * {@code timeWindow} 由请求体传入；{@code maxUserIdCount} 亦来自请求体，但实际参与校验的上限不超过 {@link #MAX_DISTINCT_USER_IDS_CAP}（服务端固定）。
 */
@Service
public class UserIdSlidingWindowGateService {

    /**
     * Redis ZSET key：{@code sw:userId:{clientId}}
     */
    private static final String REDIS_USER_ID_KEY_PREFIX = "sw:userId:";

    /**
     * 滑动时间窗口范围内可操控 UserId 的最大数量（配置参数里 {@code maxUserIdCount} 设置的再大，参与校验的也不超过该值）（这是底线）
     */
    private static final int MAX_DISTINCT_USER_IDS_CAP = 10;

    private final SlidingWindowUserIdLimiter userIdLimiter;

    public UserIdSlidingWindowGateService(SlidingWindowUserIdLimiter userIdLimiter) {
        this.userIdLimiter = userIdLimiter;
    }

    /**
     * 校验通过则写入 Redis；不通过抛出 429。
     *
     * @param clientId       客户端标识，Redis key：{@code sw:userId:}{@code clientId}
     * @param userIds        本单涉及的 UserId
     * @param maxUserIdCount 窗口内允许的最大不重复 UserId 数（来自车控限制配置；实际与 {@link #MAX_DISTINCT_USER_IDS_CAP} 取较小值再校验）
     * @param timeWindow     滑动窗口长度秒数（来自车控限制配置）
     */
    public void assertWithinUserIdWindow(String clientId, List<String> userIds, int maxUserIdCount, int timeWindow) {
        if (clientId == null || clientId.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "clientId 不能为空");
        }
        if (maxUserIdCount <= 0 || timeWindow <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "maxUserIdCount、timeWindow 须为正整数");
        }

        List<String> userIdList = normalizeUserIdList(userIds);
        // 滑动窗口Redis key
        String redisKey = REDIS_USER_ID_KEY_PREFIX + clientId;
        // 滑动窗口长度(单位：分钟)
        long windowMs = timeWindow * 1000L;
        int effectiveMaxUserIdCount = Math.min(maxUserIdCount, MAX_DISTINCT_USER_IDS_CAP);
        boolean ok = userIdLimiter.tryRecordDistinctUserIds(redisKey, userIdList, effectiveMaxUserIdCount, windowMs);
        if (!ok) {
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "滑动窗口内该 client 操作的 UserId 数量超过上限");
        }
    }

    /**
     * 删除 null/空元素，并去重。
     */
    private static List<String> normalizeUserIdList(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        Set<String> out = new LinkedHashSet<>();
        for (String v : userIds) {
            if (v != null && !v.isBlank()) {
                out.add(v);
            }
        }
        return new ArrayList<>(out);
    }
}
