package com.yz.mall.tw.vehicle;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import com.yz.mall.tw.vehicle.config.TwVehicleProperties;

/**
 * 车辆档案 Core 自动配置
 */
@Configuration
@EnableConfigurationProperties(TwVehicleProperties.class)
@ComponentScan({"com.yz.mall.tw.vehicle"})
public class TwVehicleCoreConfig {
}
