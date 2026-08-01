package com.yz.mall.demo.jaeger.a;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Jaeger Demo 服务 A 启动类（Feign 调用 B）。
 */
@EnableFeignClients
@SpringBootApplication
public class JaegerAApplication {

    public static void main(String[] args) {
        SpringApplication.run(JaegerAApplication.class, args);
    }
}
