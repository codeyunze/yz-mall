package com.yz.mall.file.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yunze
 * @date 2024/12/3 00:02
 */
@RestController
@RequestMapping("/file")
public class TestController {

    @RequestMapping("test")
    public String test(){
        return "file test!!!";
    }
}
