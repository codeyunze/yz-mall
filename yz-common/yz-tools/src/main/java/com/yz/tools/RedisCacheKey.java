package com.yz.tools;

/**
 * @author yunze
 * @date 2024/7/11 13:00
 */
public class RedisCacheKey {

    /**
     * 获取序列号信息的key
     *
     * @param prefix 序列号前缀
     * @return 序列号信息的key
     */
    public static String objUnqid(String prefix) {
        return "obj:unqid:" + prefix;
    }
}
