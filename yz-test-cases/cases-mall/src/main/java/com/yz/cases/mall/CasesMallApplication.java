package com.yz.cases.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yunze
 * @date 2023/10/29 0029 16:11
 */
@SpringBootApplication
@MapperScan(value = {"com.yz.cases.mall.mapper"})
// @EnableAspectJAutoProxy(exposeProxy = true)
@EnableScheduling
public class CasesMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasesMallApplication.class, args);
    }
}
