package com.yz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TestController
 * @Description TODO
 * @Author yunze
 * @Date 2022/11/22 0:26
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/test")
@RefreshScope
public class TestController {

    @Autowired
    private MyProperties properties;

    @Value("${txj.message}")
    private String msg;

    @RequestMapping(value = "/a")
    public String a() {
        return msg;
    }

    @RequestMapping(value = "/b")
    public String b() {
        return properties.getMessage();
    }
}
