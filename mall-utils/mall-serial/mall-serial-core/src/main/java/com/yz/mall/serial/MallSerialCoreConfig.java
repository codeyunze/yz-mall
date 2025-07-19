package com.yz.mall.serial;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @since 2025/7/18 15:38
 */
@Configuration
@MapperScan({"com.yz.mall.serial.mapper"})
@ComponentScan({"com.yz.mall.serial"})
public class MallSerialCoreConfig {
}
