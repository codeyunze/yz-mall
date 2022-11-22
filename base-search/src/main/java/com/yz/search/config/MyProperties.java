package com.yz.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName MyConfig
 * @Description TODO
 * @Author yunze
 * @Date 2022/11/22 0:43
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "txj")
public class MyProperties {

    private String message;
}
