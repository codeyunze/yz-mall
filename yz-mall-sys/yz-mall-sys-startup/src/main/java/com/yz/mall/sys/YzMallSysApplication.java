package com.yz.mall.sys;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户服务启动类
 *
 * @author yunze
 * @date 2024/6/16 星期日 23:15
 */
@SpringBootApplication
public class YzMallSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(YzMallSysApplication.class, args);
        System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }
}
