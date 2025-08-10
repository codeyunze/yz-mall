package com.yz.mall.redis;

/**
 * RLock分布式锁的key
 *
 * @author yunze
 * @date 2024/7/1 星期一 22:29
 */
public class RedissonLockKey {

    /**
     * 获取唯一序列号生成锁的key
     *
     * @param prefix 序列号前缀
     * @return 序列号锁的key
     */
    public static String keyUnqid(String prefix) {
        return "lock:unqid:" + prefix;
    }

    /**
     * 刷新权限缓存锁的key
     *
     * @return 权限缓存数据更新锁的key
     */
    public static String keyRefreshPermission() {
        return "lock:permission:button-and-api";
    }

}
