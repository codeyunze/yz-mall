package com.yz.mall.oms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @since 2025/8/7 00:17
 */
@Configuration
@MapperScan("com.yz.mall.oms.mapper")
@ComponentScan({"com.yz.mall.oms"})
public class OmsDaoConfig {
}
