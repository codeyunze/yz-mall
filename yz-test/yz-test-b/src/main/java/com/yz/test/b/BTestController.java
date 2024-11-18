package com.yz.test.b;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yunze
 * @date 2024/11/5 17:42
 */
@RestController
@RequestMapping("/b")
public class BTestController {

    @Autowired
    private AFeign feign;

    @RequestMapping("/test")
    public String test() {
        return feign.test();
    }


}
