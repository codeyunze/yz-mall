package com.yz.mall.sys;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @since 2025/7/7 18:09
 */
@Configuration
@EnableFeignClients(basePackages = {"com.yz.mall.sys.feign"})
@ComponentScan({"com.yz.mall.sys.service.impl"})
public class SysFeignConfig {
}
