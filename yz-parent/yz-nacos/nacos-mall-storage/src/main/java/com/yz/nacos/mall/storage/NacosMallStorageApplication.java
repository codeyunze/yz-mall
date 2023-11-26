package com.yz.nacos.mall.storage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.yz.*")
@MapperScan(value = {"com.yz.nacos.mall.stock.mapper"})
public class NacosMallStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosMallStorageApplication.class, args);
    }

}
