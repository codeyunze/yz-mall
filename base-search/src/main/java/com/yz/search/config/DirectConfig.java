package com.yz.search.config;

import com.yz.common.common.Constants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author : gaohan
 * @date : 2022/6/28 23:44
 */
@Configuration
public class DirectConfig {

    @Bean
    public Queue directQueue() {
        return new Queue(Constants.QUEUE_DIRECT);
    }
}
