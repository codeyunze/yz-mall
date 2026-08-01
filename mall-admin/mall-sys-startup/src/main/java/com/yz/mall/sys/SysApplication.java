package com.yz.mall.sys;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import org.dromara.dynamictp.spring.annotation.EnableDynamicTp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 系统服务启动类
 *
 * @author yunze
 * @date 2024/6/16 星期日 23:15
 */
@EnableDynamicTp
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
@ComponentScan({"com.yz.mall.sys", "com.yz.mall.serial"})
public class SysApplication {

    public static void main(String[] args) {
        SpringApplication.run(SysApplication.class, args);
    }
}
