package com.yz.mall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网关启动类
 * @author yunze
 * @date 2024/7/21 星期日 17:31
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        // System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }
}
