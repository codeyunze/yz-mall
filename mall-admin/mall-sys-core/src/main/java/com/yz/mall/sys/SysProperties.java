package com.yz.mall.sys;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 系统管理服务-配置信息
 *
 * @author yunze
 * @date 2024/11/23 星期六 17:02
 */
@Data
@ConfigurationProperties(prefix = "yz.mall.sys")
public class SysProperties {

    /**
     * 超级管理员Id
     */
    private String superAdminId = "1858113817985785856";

    /**
     * 超级管理员角色
     */
    private String superAdminRoleId = "1858098107289014272";

    /**
     * Caffeine缓存配置
     */
    private CaffeineCache caffeineCache = new CaffeineCache();

    @Data
    public static class CaffeineCache {
        /**
         * 是否启用 Caffeine 本地缓存，默认为 true。
         * 启用为 true，禁用为 false
         */
        private Boolean enable = true;

        /**
         * 最大缓存数量
         */
        private Long maximumSize = 10000L;

        /**
         * 写入后过期时间（秒）
         */
        private Long expireAfterWrite = 3600L;

        /**
         * 访问后过期时间（秒），如果为0则不启用
         */
        private Long expireAfterAccess = 0L;
    }
}
