package com.yz.mall.tw.device;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 终端 Core 自动配置
 */
@Configuration
@ComponentScan({"com.yz.mall.tw.device"})
public class TwDeviceCoreConfig {

    /**
     * MQTT 密钥摘要编码器
     *
     * @return BCrypt 编码器
     */
    @Bean
    public PasswordEncoder twDevicePasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
