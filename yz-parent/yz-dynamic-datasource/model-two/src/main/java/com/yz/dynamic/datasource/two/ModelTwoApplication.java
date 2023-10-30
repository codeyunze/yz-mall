package com.yz.dynamic.datasource.two;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = {"com.yz.dynamic.datasource.two.mapper"})
public class ModelTwoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModelTwoApplication.class, args);
    }

}
