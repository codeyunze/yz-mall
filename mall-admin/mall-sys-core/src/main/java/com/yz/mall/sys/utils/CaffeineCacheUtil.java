package com.yz.mall.sys.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.yz.mall.sys.SysProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Caffeine 本地缓存工具类
 * 封装 Caffeine 缓存的基础操作，内置 enable 控制逻辑
 *
 * @author yunze
 * @since 2025-12-05
 */
@Slf4j
@Component
public class CaffeineCacheUtil {

    private final Cache<String, Object> caffeineCache;
    private final SysProperties sysProperties;

    public CaffeineCacheUtil(Cache<String, Object> caffeineCache, SysProperties sysProperties) {
        this.caffeineCache = caffeineCache;
        this.sysProperties = sysProperties;
    }

    /**
     * 判断 Caffeine 缓存是否禁用
     *
     * @return 如果启用返回 false，禁用返回 true
     */
    private boolean isDisabled() {
        return sysProperties == null
                || sysProperties.getCaffeineCache() == null
                || Boolean.FALSE.equals(sysProperties.getCaffeineCache().getEnable());
    }

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值，如果不存在或未启用则返回 null
     */
    public Object getIfPresent(String key) {
        if (isDisabled()) {
            return null;
        }
        try {
            return caffeineCache.getIfPresent(key);
        } catch (Exception e) {
            log.error("从 Caffeine 缓存获取数据失败，key: {}", key, e);
            return null;
        }
    }

    /**
     * 获取缓存值并转换为指定类型
     *
     * @param key   缓存键
     * @param clazz 目标类型
     * @param <T>   类型参数
     * @return 缓存值，如果不存在或未启用则返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> T getIfPresent(String key, Class<T> clazz) {
        Object value = getIfPresent(key);
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 存入缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void put(String key, Object value) {
        if (isDisabled()) {
            return;
        }
        try {
            caffeineCache.put(key, value);
        } catch (Exception e) {
            log.error("存入 Caffeine 缓存失败，key: {}", key, e);
            // 缓存失败不影响主流程，只记录日志
        }
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public void invalidate(String key) {
        if (isDisabled()) {
            return;
        }
        try {
            caffeineCache.invalidate(key);
        } catch (Exception e) {
            log.error("删除 Caffeine 缓存失败，key: {}", key, e);
            // 缓存失败不影响主流程，只记录日志
        }
    }

    /**
     * 批量删除缓存
     *
     * @param keys 缓存键集合
     */
    public void invalidateAll(Iterable<String> keys) {
        if (isDisabled()) {
            return;
        }
        try {
            caffeineCache.invalidateAll(keys);
        } catch (Exception e) {
            log.error("批量删除 Caffeine 缓存失败", e);
            // 缓存失败不影响主流程，只记录日志
        }
    }

    /**
     * 清空所有缓存
     */
    public void invalidateAll() {
        if (isDisabled()) {
            return;
        }
        try {
            caffeineCache.invalidateAll();
        } catch (Exception e) {
            log.error("清空 Caffeine 缓存失败", e);
            // 缓存失败不影响主流程，只记录日志
        }
    }

    /**
     * 获取缓存统计信息（如果启用）
     *
     * @return 缓存统计信息，如果未启用则返回 null
     */
    public Object stats() {
        if (isDisabled()) {
            return null;
        }
        try {
            return caffeineCache.stats();
        } catch (Exception e) {
            log.error("获取 Caffeine 缓存统计信息失败", e);
            return null;
        }
    }
}

