package com.yz.test.a;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yunze
 * @date 2024/11/5 17:42
 */
@RestController
@RequestMapping("/a")
public class ATestController {

    @RequestMapping("/test")
    public String test() {
        return "服务A";
    }
}
