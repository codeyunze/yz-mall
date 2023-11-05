package com.yz.nacos.mall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = {"com.yz.nacos.mall.order.mapper"})
public class NacosMallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosMallOrderApplication.class, args);
    }

}
