package com.yz.mall.redis;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author yunze
 * @date 2024/7/1 星期一 22:12
 */
@Configuration
public class RedissonConfig {

    Logger log = Logger.getLogger(this.getClass().getName());

    private final RedisProperties redisProperties;

    public RedissonConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public Redisson redisson() {
        Config config = new Config();
        // 判断是否配置的是redis集群
        if (redisProperties.getCluster() != null) {
            log.info("加载redisson集群模式");
            // 获取redis集群的所有redis节点信息
            List<String> nodes = redisProperties.getCluster().getNodes();
            List<String> clusterNodes = new ArrayList<>(nodes.size());
            for (String node : nodes) {
                clusterNodes.add("redis://" + node);
            }

            config.useClusterServers().addNodeAddress(clusterNodes.toArray(new String[0]));

            if (StringUtils.hasText(redisProperties.getPassword())) {
                // 设置redis访问密码
                config.useClusterServers().setPassword(redisProperties.getPassword());
            }
        } else {
            log.info("加载redisson单机模式");

            config.useSingleServer().setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort()).setDatabase(redisProperties.getDatabase());

            if (StringUtils.hasText(redisProperties.getPassword())) {
                config.useSingleServer().setPassword(redisProperties.getPassword());
            }
        }

        return (Redisson) Redisson.create(config);
    }

}
