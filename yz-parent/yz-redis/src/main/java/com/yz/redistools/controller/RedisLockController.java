package com.yz.redistools.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
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
@RequestMapping("/lock")
public class RedisLockController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/redis")
    public String redis() {
        return "success!";
    }

    @Autowired
    private RedisProperties redisProperties;

    @RequestMapping("/info")
    public void testLock() throws JsonProcessingException {
        log.error("redis cluster: {}", new JsonMapper().writeValueAsString(redisProperties.getCluster()));
    }
}
