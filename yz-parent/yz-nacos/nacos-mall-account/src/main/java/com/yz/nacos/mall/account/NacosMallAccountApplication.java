package com.yz.nacos.mall.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(value = {"com.yz.nacos.mall.account.mapper"})
@SpringBootApplication
public class NacosMallAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosMallAccountApplication.class, args);
    }

}
