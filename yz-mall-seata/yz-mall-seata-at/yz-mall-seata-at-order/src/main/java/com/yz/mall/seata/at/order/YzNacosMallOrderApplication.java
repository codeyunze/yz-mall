package com.yz.mall.seata.at.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class YzNacosMallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(YzNacosMallOrderApplication.class, args);
    }

}
