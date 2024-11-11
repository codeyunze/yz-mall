package com.yz.mall.oms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 订单管理系统
 *
 * @author yunze
 * @date 2024/6/18 12:47
 */
@EnableFeignClients
@SpringBootApplication
public class YzMallOmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(YzMallOmsApplication.class, args);
    }
}
