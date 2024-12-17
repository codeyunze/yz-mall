package com.yz.mall.oms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 订单管理系统
 *
 * @author yunze
 * @date 2024/6/18 12:47
 */
// @ComponentScan("com.yz.mall.oms.*")
// @EnableFeignClients
@SpringBootApplication
public class YzMallOmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(YzMallOmsApplication.class, args);
    }
}
