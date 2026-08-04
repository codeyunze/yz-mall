package com.yz.mall.tw.device;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 终端 DAO 自动配置
 */
@Configuration
@MapperScan("com.yz.mall.tw.device.mapper")
@ComponentScan({"com.yz.mall.tw.device"})
public class TwDeviceDaoConfig {
}
