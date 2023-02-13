package com.yz.auth.business;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TestController
 * @Description TODO
 * @Author yunze
 * @Date 2023/2/13 23:44
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin")
public class TestController {

    @RequestMapping(value = "/demo")
    public String demo(){
        return "spring security demo";
    }
}
