package com.yz.mall.sys.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口
 * @author yunze
 * @since 2025/11/19 17:30
 */
@RequestMapping("/sys/test")
@RestController
public class SysTestController {

    @RequestMapping("/get")
    public String get() {
        return "hello world";
    }

    @RequestMapping("/fallback")
    public String fallback() {
        return "Sorry, The system not be access!!!";
    }
}
