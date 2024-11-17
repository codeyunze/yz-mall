package com.yz.mall.sys;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @date 2024/6/27 星期四 23:19
 */
@Configuration
@EnableFeignClients
@ComponentScan({"com.yz.mall.sys"})
public class YzMallSysFeignConfig {

}
