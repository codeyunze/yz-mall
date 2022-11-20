package com.yz.openinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

/**
 * @author yunze
 */
@ComponentScans(value = {
        @ComponentScan(value = "com.yz.redis.*")
})
@SpringBootApplication
public class OpenInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenInterfaceApplication.class, args);
    }
}
