package com.yz.mall.sys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @date 2024/6/19 星期三 23:54
 */
@Configuration
@MapperScan(basePackages = "com.yz.mall.sys.mapper")
@ComponentScan({"com.yz.mall.sys"})
public class YzMallSysServiceConfig {
}
