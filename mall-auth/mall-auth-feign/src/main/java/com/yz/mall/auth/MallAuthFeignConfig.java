package com.yz.mall.auth;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @since 2025/7/13 00:19
 */
@Configuration
@EnableFeignClients
@ComponentScan({"com.yz.mall.auth"})
public class MallAuthFeignConfig {
}
