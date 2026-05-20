package com.yz.mall.poc.sw.ratelimit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/**
 * 与 Redis 交互的入口：每个 client 对应一个 ZSET（member=VIN，score=最近一次出现在请求中的时间），
 * 滑动窗口的裁剪与「是否允许本次请求的 VIN 集合」在 Lua 中原子完成，避免并发下计数漂移。
 */
@Component
public class SlidingWindowVinLimiter {

    private static final String SCRIPT_PATH = "redis/sliding-window-vin-limit.lua";

    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<Long> script = new DefaultRedisScript<>();

    public SlidingWindowVinLimiter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.script.setLocation(new ClassPathResource(SCRIPT_PATH));
        this.script.setResultType(Long.class);
    }

    /**
     * 尝试在滑动窗口内登记本次请求涉及的 VIN。
     *
     * @param redisKey    该 client 的 ZSET key（一般为 {@code sw:vin:}{@code clientId}）
     * @param vins        本次请求涉及的 VIN；方法内会再次去空、去重
     * @param maxDistinct 窗口内允许同时存在的不同 VIN 个数上限
     * @param windowMs    窗口长度（毫秒）；score 早于「now - windowMs」的 member 视为已滑出窗口
     * @return true 表示未超限且已在 Redis 中更新各 VIN 的 score；false 表示本单会突破上限，Redis 不写
     */
    public boolean tryRecordDistinctVins(String redisKey, Collection<String> vins, int maxDistinct, long windowMs) {
        // 与 Lua 约定：ARGV 从第 5 个起全是 VIN，故此处先收敛为无空串、无重复的集合
        Set<String> deduped = new LinkedHashSet<>();
        if (vins != null) {
            for (String v : vins) {
                if (v != null && !v.isBlank()) {
                    deduped.add(v);
                }
            }
        }
        // 未携带任何有效 VIN：不扩展该 client 的 VIN 集合，也不访问 Redis
        if (deduped.isEmpty()) {
            return true;
        }

        long now = System.currentTimeMillis();
        // 比窗口略长，避免冷 key 或边界时钟下 member 残留无过期；与业务窗口无强一致关系，仅作 key 回收
        long ttlSec = (windowMs / 1000) + 10;
        List<String> keys = Collections.singletonList(redisKey);
        // ARGV：前 4 个为脚本固定参数，其后每个元素对应一个待登记的 VIN
        List<String> argv = new ArrayList<>(4 + deduped.size());
        argv.add(String.valueOf(now));
        argv.add(String.valueOf(windowMs));
        argv.add(String.valueOf(maxDistinct));
        argv.add(String.valueOf(ttlSec));
        argv.addAll(deduped);

        // 须传入 Object[] 作为可变参数展开；若写成 (Object) list.toArray(new String[0])，
        // 会把整个 String[] 当成「一个」实参，StringRedisSerializer 会收到 String[] 导致 ClassCastException。
        Object[] argvFlat = argv.toArray(new Object[0]);
        Long result = stringRedisTemplate.execute(script, keys, argvFlat);
        return result == 1L;
    }
}
