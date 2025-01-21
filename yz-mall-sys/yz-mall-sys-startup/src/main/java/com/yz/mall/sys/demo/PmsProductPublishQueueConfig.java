package com.yz.mall.sys.demo;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 商品管理-商品上架待办
 * @author yunze
 * @date 2025/1/21 22:42
 */
@Configuration
public class PmsProductPublishQueueConfig {

    public static final String QUEUE_NAME = "pms_product_publish";

    @Bean
    public Queue queuePmsProductPublishStart() {
        return new Queue(QUEUE_NAME + "_start");
    }

    @Bean
    public Queue queuePmsProductPublishEnd() {
        return new Queue(QUEUE_NAME + "_end");
    }

    @Bean
    public Binding bindQueuePmsProductPublishStartToPmsExchange() {
        return BindingBuilder.bind(queuePmsProductPublishStart()).to(SysPendingTaskQueueConfig.setSysTaskTopicExchange()).with(QUEUE_NAME + "_start_key");
    }

    @Bean
    public Binding bindQueuePmsProductPublishEndToPmsExchange() {
        return BindingBuilder.bind(queuePmsProductPublishEnd()).to(SysPendingTaskQueueConfig.setSysTaskTopicExchange()).with(QUEUE_NAME + "_end_key");
    }
}
