package com.yz.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * @author : gaohan
 * @date : 2022/8/28 20:41
 */
@SpringBootApplication
// @EnableWebSecurity
@EnableAuthorizationServer
// @ComponentScan(value = "com.yz.redis.*")
@MapperScans(value = {
        @MapperScan(value = "com.yz.*.*.mapper")
})
// @ComponentScans(value = {
//         @ComponentScan(value = "com.gh.redis.*"),
//         @ComponentScan(value = "com.gh.common.*")
// })
public class BaseSecurityAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseSecurityAuthApplication.class, args);
    }
}
