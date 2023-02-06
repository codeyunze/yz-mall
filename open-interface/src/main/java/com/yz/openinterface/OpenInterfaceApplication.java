package com.yz.openinterface;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

/**
 * @author yunze
 */
@MapperScans(value = {
        @MapperScan(value = "com.yz.openinterface.business.*.*.mapper"),
        @MapperScan(value = "com.yz.openinterface.business.*.*.dao")
})
@ComponentScans(value = {
        @ComponentScan(value = "com.yz.redis.*")
})
@SpringBootApplication
public class OpenInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenInterfaceApplication.class, args);
    }
}
