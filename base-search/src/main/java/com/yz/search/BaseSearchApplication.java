package com.yz.search;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

/**
 * @ClassName BaseSearchApplication
 * @Description TODO
 * @Author yunze
 * @Date 2022/11/20 23:55
 * @Version 1.0
 */
@SpringBootApplication
@EnableConfigurationProperties
@MapperScan("com.yz.search.business.*.mapper")
@ComponentScans(value = {
        @ComponentScan(value = "com.yz.redis.*")
})
public class BaseSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseSearchApplication.class, args);
    }
}
