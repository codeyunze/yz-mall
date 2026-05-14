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
 * {@code maxVinCount}、{@code timeWindow} 须由调用方从请求体解析后传入（正整数）。
 */
@Service
public class VinSlidingWindowGateService {

    /** Redis ZSET key：{@code sw:vin:{clientId}}，所有命中校验的 URI 共用同一计数 */
    private static final String REDIS_VIN_KEY_PREFIX = "sw:vin:";

    private final SlidingWindowVinLimiter vinLimiter;

    public VinSlidingWindowGateService(SlidingWindowVinLimiter vinLimiter) {
        this.vinLimiter = vinLimiter;
    }

    /**
     * 校验通过则写入 Redis；不通过抛出 429。
     *
     * @param clientId    客户端标识，Redis key：{@code sw:vin:}{@code clientId}
     * @param vins        本单涉及的 VIN；允许 null/空，表示不扩展窗口内集合
     * @param maxVinCount 窗口内允许的最大不重复 VIN 种类数（来自请求体）
     * @param timeWindow  滑动窗口长度秒数（来自请求体）
     */
    public void assertWithinVinWindow(String clientId, List<String> vins, int maxVinCount, int timeWindow) {
        if (clientId == null || clientId.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "clientId 不能为空");
        }
        if (maxVinCount <= 0 || timeWindow <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "maxVinCount、timeWindow 须为正整数");
        }

        List<String> vinList = normalizeVinList(vins);
        String redisKey = REDIS_VIN_KEY_PREFIX + clientId;
        long windowMs = timeWindow * 1000L;
        boolean ok = vinLimiter.tryRecordDistinctVins(redisKey, vinList, maxVinCount, windowMs);
        if (!ok) {
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "滑动窗口内该 client 操作的 VIN 种类数超过上限");
        }
    }

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
