package com.yz.openinterface.config;

import com.yz.common.common.Constants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : yunze
 * @date : 2022/11/24 23:40
 */
@Configuration
public class DirectConfig {

    @Bean
    public Queue directQueue() {
        return new Queue(Constants.QUEUE_DIRECT);
    }
}
