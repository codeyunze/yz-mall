package com.yz.unqid;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @date 2024/6/23 星期日 22:47
 */
@Configuration
@EnableFeignClients
@ComponentScan({"com.yz.unqid"})
public class YzUnqidFeignConfig {
}
