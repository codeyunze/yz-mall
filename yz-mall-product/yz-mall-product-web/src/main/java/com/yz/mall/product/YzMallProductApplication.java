package com.yz.mall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yunze
 * @date 2024/06/16
 */
@SpringBootApplication
// @MapperScan(value = {"com.yz.cases.mall.mapper"})
// @EnableAspectJAutoProxy(exposeProxy = true)
@EnableScheduling
public class YzMallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(YzMallProductApplication.class, args);
    }
}
