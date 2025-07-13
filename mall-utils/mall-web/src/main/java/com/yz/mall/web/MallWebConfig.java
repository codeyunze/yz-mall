package com.yz.mall.web;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * @author yunze
 * @since 2025/7/13 22:53
 */
@EnableAutoConfiguration
@Configuration
@ComponentScan({"com.yz.mall.web"})
public class MallWebConfig {
}
