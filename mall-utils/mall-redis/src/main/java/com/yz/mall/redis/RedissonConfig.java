package com.yz.mall.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Redisson 客户端配置。
 *
 * @author yunze
 * @date 2024/7/1 星期一 22:12
 */
@Slf4j
@Configuration
public class RedissonConfig {

    private final RedisProperties redisProperties;

    public RedissonConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    /**
     * 创建 Redisson 客户端。
     *
     * @return Redisson 实例
     */
    @Bean
    public Redisson redisson() {
        Config config = new Config();
        boolean passwordConfigured = StringUtils.hasText(redisProperties.getPassword());
        // 判断是否配置的是redis集群
        if (redisProperties.getCluster() != null) {
            // 获取redis集群的所有redis节点信息
            List<String> nodes = redisProperties.getCluster().getNodes();
            List<String> clusterNodes = new ArrayList<>(nodes.size());
            for (String node : nodes) {
                clusterNodes.add("redis://" + node);
            }
            log.info(">>>>>>>>>>> redisson config init. mode=cluster, nodes={}, passwordConfigured={}", nodes, passwordConfigured);
            config.useClusterServers().addNodeAddress(clusterNodes.toArray(new String[0]));
            if (passwordConfigured) {
                // 设置redis访问密码
                config.setPassword(redisProperties.getPassword());
            }
        } else {
            String address = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();
            log.info(">>>>>>>>>>> redisson config init. mode=single, address={}, database={}, passwordConfigured={}",
                    address, redisProperties.getDatabase(), passwordConfigured);
            config.useSingleServer().setAddress(address).setDatabase(redisProperties.getDatabase());
            if (passwordConfigured) {
                config.setPassword(redisProperties.getPassword());
            }
        }
        return (Redisson) Redisson.create(config);
    }

}
