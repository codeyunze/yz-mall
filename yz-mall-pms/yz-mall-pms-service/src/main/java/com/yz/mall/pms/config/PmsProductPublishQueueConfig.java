package com.yz.mall.pms.config;

import com.yz.mall.sys.business.task.AbstractSysPendingTasksQueueConfig;
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
public class PmsProductPublishQueueConfig extends AbstractSysPendingTasksQueueConfig {

    public static final String QUEUE_NAME = "pms_product_publish";
    private static final String START_KEY = QUEUE_NAME + "_start_key";
    private static final String END_KEY = QUEUE_NAME + "_end_key";

    @Bean("queuePmsProductPublishStart")
    @Override
    protected Queue queueStart() {
        return new Queue(QUEUE_NAME + "_start");
    }

    @Bean("queuePmsProductPublishEnd")
    @Override
    protected Queue queueEnd() {
        return new Queue(QUEUE_NAME + "_end");
    }

    @Bean("bindQueuePmsProductPublishStartToPmsExchange")
    @Override
    protected Binding bindQueueStartToPmsExchange() {
        return BindingBuilder.bind(queueStart()).to(super.setSysTaskTopicExchange()).with(START_KEY);
    }

    @Bean("bindQueuePmsProductPublishEndToPmsExchange")
    @Override
    protected Binding bindQueueEndToPmsExchange() {
        return BindingBuilder.bind(queueEnd()).to(super.setSysTaskTopicExchange()).with(END_KEY);
    }
}
