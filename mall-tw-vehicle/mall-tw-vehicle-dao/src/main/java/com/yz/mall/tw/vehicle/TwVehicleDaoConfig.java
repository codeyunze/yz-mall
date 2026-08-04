package com.yz.mall.tw.vehicle;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 车辆档案 DAO 自动配置
 */
@Configuration
@MapperScan("com.yz.mall.tw.vehicle.mapper")
@ComponentScan({"com.yz.mall.tw.vehicle"})
public class TwVehicleDaoConfig {
}
