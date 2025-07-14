package com.yz.mall.sys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @since 2025/7/7 18:09
 */
@EnableCaching
@Configuration
@EnableConfigurationProperties(SysProperties.class)
@ComponentScan({"com.yz.mall.sys"})
public class MallSysCoreConfig {
}
