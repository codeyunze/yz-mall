package com.yz.test.c;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author yunze
 * @date 2024/11/5 17:39
 */
@EnableFeignClients
@SpringBootApplication
public class CApplication {
    public static void main(String[] args) {
        SpringApplication.run(CApplication.class, args);
    }
}
