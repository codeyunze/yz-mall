package com.yz.mall.pms;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @since 2025/8/8 11:20
 */
@Configuration
@EnableFeignClients
@ComponentScan({"com.yz.mall.pms"})
public class PmsFeignConfig {
}
