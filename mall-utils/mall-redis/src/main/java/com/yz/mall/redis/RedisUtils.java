package com.yz.mall.redis;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yunze
 * @date 2024/12/14 星期六 22:39
 */
@Component
public class RedisUtils {

    private final RedisTemplate<String, Object> defaultRedisTemplate;

    public RedisUtils(RedisTemplate<String, Object> defaultRedisTemplate) {
        this.defaultRedisTemplate = defaultRedisTemplate;
    }

    /**
     * 删除匹配条件的的缓存信息
     *
     * @param pattern 模糊匹配key
     */
    public void deleteByPatternKey(String pattern) {
        Cursor<byte[]> cursor = defaultRedisTemplate.execute((RedisCallback<? extends Cursor<byte[]>>) connection ->
                connection.scan(ScanOptions.scanOptions().match(pattern).count(100).build()));
        while (true) {
            assert cursor != null;
            if (!cursor.hasNext()) break;
            byte[] keyBytes = cursor.next();
            String key = new String(keyBytes);
            System.out.println("Found key: " + key);
            defaultRedisTemplate.delete(key);
        }
    }

    /**
     * 获取匹配条件的缓存key
     *
     * @param pattern 模糊匹配key
     * @return 缓存信息的key
     */
    public List<String> getKeysByPattern(String pattern) {
        Cursor<byte[]> cursor = defaultRedisTemplate.execute((RedisCallback<? extends Cursor<byte[]>>) connection ->
                connection.scan(ScanOptions.scanOptions().match(pattern).count(100).build()));

        assert cursor != null;
        return cursor.stream()
                .map(String::new)
                .collect(Collectors.toList());
    }
}
