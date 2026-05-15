package com.yz.mall.poc.sw.controller;

import com.yz.mall.poc.sw.dto.TestARequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yunze
 * @since 2026/5/13 09:12
 */
@RestController
@RequestMapping(value = "/poc/test")
public class TestController {

    @PostMapping("a")
    public String a(
            @RequestHeader String clientId,
            @RequestBody TestARequest req) {
        return "success: clientId="
                + clientId
                + ", vinCount="
                + req.getVins().size()
                + ", maxVinCount="
                + req.getMaxVinCount()
                + ", timeWindow="
                + req.getTimeWindow();
    }
}
