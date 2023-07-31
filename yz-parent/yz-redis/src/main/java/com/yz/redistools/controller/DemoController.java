package com.yz.redistools.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yunze
 * @date 2023/7/31 0031 23:20
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping("/redis")
    public String redis(){
        return "success!";
    }
}
