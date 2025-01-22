package com.yz.mall.sys.business.task;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 待办任务消息队列配置抽象类
 *
 * @author yunze
 * @date 2025/1/21 18:42
 */
@Configuration
public abstract class AbstractSysPendingTasksQueueConfig {

    public static final String EXCHANGE_NAME = "sys_task_exchange";

    @Bean
    public TopicExchange setSysTaskTopicExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    protected abstract Queue queueStart();

    protected abstract Queue queueEnd();

    protected abstract Binding bindQueueStartToPmsExchange();

    protected abstract Binding bindQueueEndToPmsExchange();
}
