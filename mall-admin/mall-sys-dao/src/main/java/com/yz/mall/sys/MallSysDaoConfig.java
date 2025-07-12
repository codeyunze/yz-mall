package com.yz.mall.sys;

import org.mybatis.spring.annotation.MapperScan;
// import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @since 2025/7/7 18:06
 */
@Configuration
// @EnableCaching
@MapperScan(basePackages = "com.yz.mall.sys.mapper")
@ComponentScan({"com.yz.mall.sys"})
public class MallSysDaoConfig {
}
