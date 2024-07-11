package com.yz.unqid.scheduler;

import com.yz.tools.RedisCacheKey;
import com.yz.unqid.service.SysUnqidService;
import org.redisson.client.RedisConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 序列号缓存数据持久化
 * @author yunze
 * @date 2024/7/11 12:56
 */
@Component
public class ScheduleCachePersistence {

    private static final Logger log = LoggerFactory.getLogger(ScheduleCachePersistence.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SysUnqidService service;

    @Scheduled(fixedDelay = 1000 * 10)
    public void scheduleCachePersistence() {
        log.info("缓存数据持久化");
        Set<String> keys = redisTemplate.keys(RedisCacheKey.objUnqid("*"));
        for (String key : keys) {
            log.info(key);
            // 截取序列号前缀
            String prefix = key.substring(key.lastIndexOf(":"));

            // 分辨是新增还是更新
            if (service.exists(prefix)) {
                log.info("更新");
            } else {
                log.info("新增");
            }
        }
    }
}
