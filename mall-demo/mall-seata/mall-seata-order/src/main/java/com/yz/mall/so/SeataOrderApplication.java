package com.yz.mall.so;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 用户服务启动类
 *
 * @author yunze
 * @date 2024/6/16 星期日 23:15
 */
@EnableFeignClients
@SpringBootApplication
public class SeataOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataOrderApplication.class, args);
    }
}
