package com.yz.mall.seata.tcc.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class YzMallSeataTccOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(YzMallSeataTccOrderApplication.class, args);
    }

}
