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
 * 滑动窗口 VIN 种类校验，供 {@link com.yz.mall.poc.sw.web.VinSlidingWindowGuardFilter} 调用。
 * <p>
 * {@code timeWindow} 由请求体传入；{@code maxVinCount} 亦来自请求体，但实际参与校验的上限不超过 {@link #MAX_DISTINCT_VINS_CAP}（服务端固定）。
 */
@Service
public class VinSlidingWindowGateService {

    /**
     * Redis ZSET key：{@code sw:vin:{clientId}}
     */
    private static final String REDIS_VIN_KEY_PREFIX = "sw:vin:";

    /**
     * 滑动时间窗口范围内可操控vin的最大数量（配置参数里 {@code maxVinCount} 设置的再大，参与校验的也不超过该值）（这是底线）
     */
    private static final int MAX_DISTINCT_VINS_CAP = 10;

    private final SlidingWindowVinLimiter vinLimiter;

    public VinSlidingWindowGateService(SlidingWindowVinLimiter vinLimiter) {
        this.vinLimiter = vinLimiter;
    }

    /**
     * 校验通过则写入 Redis；不通过抛出 429。
     *
     * @param clientId    客户端标识，Redis key：{@code sw:vin:}{@code clientId}
     * @param vins        本单涉及的 VIN
     * @param maxVinCount 窗口内允许的最大不重复 VIN 数（来自车控限制配置；实际与 {@link #MAX_DISTINCT_VINS_CAP} 取较小值再校验）
     * @param timeWindow  滑动窗口长度秒数（来自车控限制配置）
     */
    public void assertWithinVinWindow(String clientId, List<String> vins, int maxVinCount, int timeWindow) {
        if (clientId == null || clientId.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "clientId 不能为空");
        }
        if (maxVinCount <= 0 || timeWindow <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "maxVinCount、timeWindow 须为正整数");
        }

        List<String> vinList = normalizeVinList(vins);
        // 滑动窗口Redis key
        String redisKey = REDIS_VIN_KEY_PREFIX + clientId;
        // 滑动窗口长度(单位：分钟)
        long windowMs = timeWindow * 1000L;
        int effectiveMaxVinCount = Math.min(maxVinCount, MAX_DISTINCT_VINS_CAP);
        boolean ok = vinLimiter.tryRecordDistinctVins(redisKey, vinList, effectiveMaxVinCount, windowMs);
        if (!ok) {
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "滑动窗口内该 client 操作的 VIN 数量超过上限");
        }
    }

    /**
     * 删除 null/空元素，并去重。
     */
    private static List<String> normalizeVinList(List<String> vins) {
        if (vins == null || vins.isEmpty()) {
            return List.of();
        }
        Set<String> out = new LinkedHashSet<>();
        for (String v : vins) {
            if (v != null && !v.isBlank()) {
                out.add(v);
            }
        }
        return new ArrayList<>(out);
    }
}
