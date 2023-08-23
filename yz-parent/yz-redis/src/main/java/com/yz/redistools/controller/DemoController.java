package com.yz.redistools.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yunze
 * @date 2023/7/31 0031 23:20
 */
@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping("/redis")
    public String redis() {
        return "success!";
    }


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/test_sentinel")
    public void testSentinel() {
        int i = 1;
        while (true) {
            try {
                stringRedisTemplate.opsForValue().set("test-" + i, String.valueOf(i));
                log.info("设置key：{}", "test-" + i);
                i++;
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("出现异常：{}", e.getMessage());
            }
        }
    }

}
