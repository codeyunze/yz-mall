package com.yz.test.b;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yunze
 * @date 2024/11/5 17:45
 */
@FeignClient(name = "yz-test-a", url = "http://127.0.0.1:20001", fallbackFactory = AFeignFallback.class)
public interface AFeign {

    @RequestMapping("/a/test")
    public String test();
}
