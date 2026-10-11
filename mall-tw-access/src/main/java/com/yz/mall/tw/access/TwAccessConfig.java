package com.yz.mall.tw.access;

import com.yz.mall.tw.access.config.TwAccessProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * mall-tw-access 统一配置
 */
@Configuration
@EnableConfigurationProperties(TwAccessProperties.class)
@EnableFeignClients(basePackages = "com.yz.mall.tw.access.feign")
public class TwAccessConfig {
}
