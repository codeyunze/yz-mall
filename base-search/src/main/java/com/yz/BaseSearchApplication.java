package com.yz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @ClassName BaseSearchApplication
 * @Description TODO
 * @Author yunze
 * @Date 2022/11/20 23:55
 * @Version 1.0
 */
@SpringBootApplication
@EnableConfigurationProperties
public class BaseSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseSearchApplication.class, args);
    }
}
