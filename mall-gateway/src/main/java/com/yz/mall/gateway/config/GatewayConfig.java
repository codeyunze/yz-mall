package com.yz.mall.gateway.config;

import com.yz.mall.redis.MallRedisConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 网关配置类
 * 导入必要的配置
 *
 * @author yunze
 * @since 2025-12-05
 */
@Configuration
@ComponentScan(basePackages = {
        "com.yz.mall.gateway",
        "com.yz.mall.redis"
})
@Import(MallRedisConfig.class)
public class GatewayConfig {
}

