package com.yz.mall.tw;

import com.yz.mall.tw.config.TwVehicleProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * mall-tw 统一配置：Mapper 扫描、车辆属性、终端密钥编码器。
 */
@Configuration
@MapperScan("com.yz.mall.tw.mapper")
@EnableConfigurationProperties(TwVehicleProperties.class)
public class TwConfig {

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
