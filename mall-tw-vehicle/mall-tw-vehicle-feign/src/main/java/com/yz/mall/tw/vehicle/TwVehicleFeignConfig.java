package com.yz.mall.tw.vehicle;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 车辆档案 Feign 自动配置
 */
@Configuration
@EnableFeignClients
@ComponentScan({"com.yz.mall.tw.vehicle"})
public class TwVehicleFeignConfig {
}
