package com.yz.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * @author : gaohan
 * @date : 2022/8/28 20:41
 */
@SpringBootApplication
// @EnableWebSecurity
@EnableAuthorizationServer
@MapperScans(value = {
        @MapperScan(value = "com.yz.auth.business.*.mapper")
})
@ComponentScans(value = {
        @ComponentScan(value = "com.yz.redis.*"),
        @ComponentScan(value = "com.yz.common.*")
})
public class BaseSecurityAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseSecurityAuthApplication.class, args);
    }
}
