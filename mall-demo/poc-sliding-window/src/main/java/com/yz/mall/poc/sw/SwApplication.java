package com.yz.mall.poc.sw;

import com.yz.mall.poc.sw.guard.SwVinGuardProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 前置校验服务：可配置路径上的滑动窗口 VIN 校验（过滤器）；不包含业务接口与 HTTP 转发。
 *
 * @author yunze
 * @since 2026/05/13 09:10
 */
@SpringBootApplication
@EnableConfigurationProperties(SwVinGuardProperties.class)
public class SwApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwApplication.class, args);
    }
}
