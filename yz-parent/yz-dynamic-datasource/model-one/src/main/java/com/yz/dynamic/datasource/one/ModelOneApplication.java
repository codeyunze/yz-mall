package com.yz.dynamic.datasource.one;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author yunze
 * @date 2023/10/29 0029 16:11
 */
@SpringBootApplication
@MapperScan(value = {"com.yz.dynamic.datasource.one.mapper"})
@EnableAspectJAutoProxy(exposeProxy=true)
public class ModelOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModelOneApplication.class, args);
    }
}
