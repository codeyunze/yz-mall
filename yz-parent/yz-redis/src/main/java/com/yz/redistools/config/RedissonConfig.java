package com.yz.redistools.config;

import org.redisson.Redisson;
import org.redisson.config.ClusterServersConfig;
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

    /**
     * redisTemplate 序列化使用的jdkSerializeable, 存储二进制字节码, 所以自定义序列化类
     *
     * @param redisConnectionFactory
     * @return
     */
    /*@Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }*/
}
