package com.yz.mall.poc.sw.controller;

import cn.hutool.json.JSONObject;
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
            @RequestHeader String vin,
            @RequestBody TestARequest req) {
        JSONObject result = new JSONObject();

        JSONObject json = new JSONObject();
        json.put("clientId", clientId);
        json.put("vin", vin);
        json.put("vinCount", req.getVins().size());
        json.put("maxVinCount", req.getMaxVinCount());
        json.put("timeWindow", req.getTimeWindow());

        result.put("code", 0);
        result.put("data", json);
        return result.toStringPretty();
    }
}
