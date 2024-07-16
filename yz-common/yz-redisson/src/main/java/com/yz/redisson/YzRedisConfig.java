package com.yz.redisson;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis缓存信息序列化调整。
 * redis默认的序列化方式是JdkSerializationRedisSerializer,序列化后的数据体积较大，而且不便于阅读。
 * 所以需要将其序列化方式设置为StringRedisSerializer或Jackson2JsonRedisSerializer。
 * 字符串序列化简单快速，而JSON序列化则提供了更好的跨语言兼容性和数据可读性。
 *
 * @author yunze
 * @date 2024/7/9 星期二 23:32
 */
@Configuration
public class YzRedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 使用StringRedisSerializer来序列化和反序列化Redis的key
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 使用Jackson2JsonRedisSerializer序列化和反序列化Redis的value值
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
