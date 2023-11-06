package com.yz.nacos.mall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan(value = {"com.yz.nacos.mall.order.mapper"})
@EnableFeignClients
@SpringBootApplication
public class NacosMallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosMallOrderApplication.class, args);
    }

}
