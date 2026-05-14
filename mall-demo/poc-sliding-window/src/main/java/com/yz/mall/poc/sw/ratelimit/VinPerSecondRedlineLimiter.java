package com.yz.mall.poc.sw.ratelimit;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 硬编码红线：同一 {@code clientId} 在<strong>自然秒</strong>内累计登记的 VIN 条数（按请求内去重后的种类数计）不得超过上限。
 * <p>
 * 与业务侧传入的 {@code maxVinCount}/{@code timeWindow} 无关，作为最后一道兜底；需在滑动窗口校验失败时调用返回的 rollback。
 */
@Component
public class VinPerSecondRedlineLimiter {

    /** 每自然秒、每 client 允许累计的最大 VIN 条数（红线，硬编码） */
    private static final int MAX_VINS_PER_NATURAL_SECOND = 10;

    private static final String KEY_PREFIX = "sw:redline:vinsec:";

    private final StringRedisTemplate stringRedisTemplate;

    public VinPerSecondRedlineLimiter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 尝试为本次请求预留 {@code distinctVinCount} 个「秒级额度」。
     *
     * @return 若通过则返回 {@link Optional#of} 携带需在滑动窗口失败时执行的回滚；若不通过则 {@link Optional#empty()}
     */
    public Optional<Runnable> tryReserve(String clientId, int distinctVinCount) {
        if (distinctVinCount <= 0) {
            return Optional.of(() -> {});
        }
        if (clientId == null || clientId.isBlank()) {
            return Optional.of(() -> {});
        }

        long sec = System.currentTimeMillis() / 1000L;
        String key = KEY_PREFIX + clientId + ":" + sec;

        Long after = stringRedisTemplate.opsForValue().increment(key, distinctVinCount);
        if (after != null && after > MAX_VINS_PER_NATURAL_SECOND) {
            stringRedisTemplate.opsForValue().increment(key, -distinctVinCount);
            return Optional.empty();
        }
        stringRedisTemplate.expire(key, Duration.ofSeconds(3));
        return Optional.of(() -> stringRedisTemplate.opsForValue().increment(key, -distinctVinCount));
    }
}
