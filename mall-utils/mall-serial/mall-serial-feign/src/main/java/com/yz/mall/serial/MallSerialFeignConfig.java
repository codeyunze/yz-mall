package com.yz.mall.serial;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @since 2025/7/18 15:38
 */
@Configuration
@EnableFeignClients
@ComponentScan({"com.yz.mall.serial"})
public class MallSerialFeignConfig {
}
