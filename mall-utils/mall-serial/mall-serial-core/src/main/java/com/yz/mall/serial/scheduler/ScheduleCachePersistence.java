package com.yz.mall.serial.scheduler;

import com.yz.mall.redis.RedisCacheKey;
import com.yz.mall.serial.service.SerialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 流水号缓存数据持久化
 *
 * @author yunze
 * @date 2024/7/11 12:56
 */
@Component
public class ScheduleCachePersistence {

    private static final Logger log = LoggerFactory.getLogger(ScheduleCachePersistence.class);

    @Resource
    private RedisTemplate<String, Object> defaultRedisTemplate;

    @Resource(name = "serialServiceImpl")
    private SerialService service;

    /**
     * 每30秒持久化一次
     */
    @Scheduled(fixedDelay = 1000 * 30)
    public void scheduleCachePersistence() {
        log.info("缓存数据持久化");
        Set<String> keys = defaultRedisTemplate.keys(RedisCacheKey.objUnqid("*"));
        assert keys != null;
        for (String key : keys) {
            log.info(key);
            // 截取序列号前缀
            String prefix = key.substring(key.lastIndexOf(":") + 1);

            // 缓存数据持久存储到mysql
            service.cachePersistence(prefix);
        }
    }
}
