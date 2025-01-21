package com.yz.mall.sys.demo;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 供应链管理-采购申请待办
 * @author yunze
 * @date 2025/1/21 22:42
 */
@Configuration
public class ScmPurchaseQueueConfig {

    public static final String QUEUE_NAME = "scm_purchase";

    @Bean
    public Queue queueScmPurchaseStart() {
        return new Queue(QUEUE_NAME + "_start");
    }

    @Bean
    public Queue queueScmPurchaseEnd() {
        return new Queue(QUEUE_NAME + "_end");
    }

    @Bean
    public Binding bindQueueScmPurchaseStartToPmsExchange() {
        return BindingBuilder.bind(queueScmPurchaseStart()).to(SysPendingTaskQueueConfig.setSysTaskTopicExchange()).with(QUEUE_NAME + "_start_key");
    }

    @Bean
    public Binding bindQueueScmPurchaseEndToPmsExchange() {
        return BindingBuilder.bind(queueScmPurchaseEnd()).to(SysPendingTaskQueueConfig.setSysTaskTopicExchange()).with(QUEUE_NAME + "_end_key");
    }
}
