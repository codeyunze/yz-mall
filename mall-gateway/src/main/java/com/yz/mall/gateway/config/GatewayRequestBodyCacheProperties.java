package com.yz.mall.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 网关请求体缓存配置。
 *
 * @author yunze
 * @date 2026/9/21
 */
@Data
@Component
@ConfigurationProperties(prefix = "yz.gateway.request-body-cache")
public class GatewayRequestBodyCacheProperties {

    /**
     * 是否缓存请求体并回写给下游，默认开启
     */
    private boolean enabled = true;

    /**
     * 读取并缓存请求体的最大字节数，超出则跳过（避免大文件撑爆内存）
     */
    private int maxBodyBytes = 1024 * 1024;
}
