package com.yz.mall.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @date 2024/6/19 星期三 23:54
 */
@Configuration
@MapperScan(basePackages = "com.yz.mall.user.mapper")
@ComponentScan({"com.yz.mall.user"})
public class YzMallUserServiceConfig {
}
