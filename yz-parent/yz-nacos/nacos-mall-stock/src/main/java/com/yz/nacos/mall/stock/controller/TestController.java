package com.yz.nacos.mall.stock.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yunze
 * @date 2023/11/5 0005 15:09
 */
@RestController
@RequestMapping("/mallStock")
public class TestController {

    @RequestMapping("test")
    public String test() {
        return "mall-stock service is fine!";
    }
}
