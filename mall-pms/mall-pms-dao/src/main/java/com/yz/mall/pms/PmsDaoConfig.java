package com.yz.mall.pms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @since 2025/8/7 11:58
 */
@Configuration
@MapperScan("com.yz.mall.pms.mapper")
@ComponentScan({"com.yz.mall.pms"})
public class PmsDaoConfig {
}
