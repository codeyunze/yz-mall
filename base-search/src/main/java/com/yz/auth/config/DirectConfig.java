package com.yz.auth.config;

import com.yz.common.Constants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author : yunze
 * @date : 2022/6/28 23:44
 */
@Configuration
public class DirectConfig {

    @Bean
    public Queue directQueue() {
        return new Queue(Constants.QUEUE_DIRECT);
    }
}
