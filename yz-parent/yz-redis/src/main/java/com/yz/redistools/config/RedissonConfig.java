package com.yz.redistools.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : yunze
 * @date : 2023/9/19 12:39
 */
@Component
public class RedissonConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public Redisson redisson() {
        Config config = new Config();

        if (redisProperties.getCluster() != null) {
            // 集群模式配置
            List<String> nodes = redisProperties.getCluster().getNodes();
            List<String> clusterNodes = new ArrayList<>();

            nodes.forEach(node -> {
                clusterNodes.add("redis://" + node);
            });

            config.useClusterServers().addNodeAddress(clusterNodes.toArray(new String[0]));

            if (StringUtils.hasText(redisProperties.getPassword())) {
                // 设置密码，如果有配置该信息
                config.useClusterServers().setPassword(redisProperties.getPassword());
            }
        } else {
            // 单节点模式配置
            config.useSingleServer().setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort()).setDatabase(redisProperties.getDatabase());

            if (StringUtils.hasText(redisProperties.getPassword())) {
                // 设置密码，如果有配置该信息
                config.useSingleServer().setPassword(redisProperties.getPassword());
            }
        }
        return (Redisson) Redisson.create(config);
    }
}
