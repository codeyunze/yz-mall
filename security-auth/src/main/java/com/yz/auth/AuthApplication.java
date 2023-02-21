package com.yz.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

/**
 * @ClassName ${NAME}
 * @Description TODO
 * @Author yunze
 * @Date ${DATE} ${TIME}
 * @Version 1.0
 */
@MapperScans(value = {
        @MapperScan(value = "com.yz.auth.business.*.dao"),
        @MapperScan(value = "com.yz.auth.business.*.*.*.dao")
})
@ComponentScans(value = {
        @ComponentScan(value = "com.yz.redis.*")
})
@SpringBootApplication
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}