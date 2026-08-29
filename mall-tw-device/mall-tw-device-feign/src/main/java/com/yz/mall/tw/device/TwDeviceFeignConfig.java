package com.yz.mall.tw.device;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 终端 Feign 自动配置
 */
@Configuration
@EnableFeignClients
@ComponentScan({"com.yz.mall.tw.device"})
public class TwDeviceFeignConfig {
}
