package com.yz.mall;

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
public class YzPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(YzPmsApplication.class, args);
    }
}
