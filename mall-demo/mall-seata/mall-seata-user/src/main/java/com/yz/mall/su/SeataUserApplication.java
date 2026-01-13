package com.yz.mall.su;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 用户服务启动类
 *
 * @author yunze
 * @date 2024/6/16 星期日 23:15
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement
public class SeataUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataUserApplication.class, args);
    }
}
