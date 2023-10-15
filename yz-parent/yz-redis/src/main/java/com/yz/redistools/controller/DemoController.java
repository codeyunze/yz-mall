package com.yz.redistools.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author yunze
 * @date 2023/7/31 0031 23:20
 */
@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoController {

    /**
     * 当前版本
     */
    @Value("${server.version}")
    private String serviceVersion;

    @RequestMapping("/redis")
    public String redis() {
        log.info("Current service version {}", serviceVersion);
        return "success!";
    }


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/test_sentinel")
    public void testSentinel() {
        int i = 1;
        while (true) {
            try {
                stringRedisTemplate.opsForValue().set("test-" + i, String.valueOf(i), 10, TimeUnit.MINUTES);
                log.info("设置key：{}", "test-" + i);
                i++;
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("出现异常：{}", e.getMessage());
            }
        }
    }

    @RequestMapping("/test_cluster")
    public void testCluster() {
        int i = 1;
        while (true) {
            try {
                stringRedisTemplate.opsForValue().set("test-cluster-" + i, String.valueOf(i));
                log.info("设置key：{}", "test-" + i);
                Thread.sleep(10);
                String val = stringRedisTemplate.opsForValue().get("test-cluster-" + i);
                i++;
                log.info("获取{}值: {}", "test-" + i, val);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("出现异常：{}", e.getMessage());
            }
        }
    }

}
