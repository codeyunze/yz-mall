package com.yz.seata.mall.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //(scanBasePackages = "com.yz.*")
public class YzNacosMallStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(YzNacosMallStorageApplication.class, args);
    }

}
