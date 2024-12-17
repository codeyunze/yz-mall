package com.yz.mall.gateway;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yunze
 * @date 2024/7/21 星期日 17:31
 */
@SpringBootApplication
public class YzGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(YzGatewayApplication.class, args);
        System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }
}
