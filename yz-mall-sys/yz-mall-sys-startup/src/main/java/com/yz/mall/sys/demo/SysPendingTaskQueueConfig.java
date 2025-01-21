package com.yz.mall.sys.demo;


import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 待办任务消息队列配置
 *
 * @author yunze
 * @date 2025/1/21 18:42
 */
@Configuration
public class SysPendingTaskQueueConfig {

    public static final String EXCHANGE_NAME = "sys_task_exchange";

    @Bean
    public static TopicExchange setSysTaskTopicExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }
}
