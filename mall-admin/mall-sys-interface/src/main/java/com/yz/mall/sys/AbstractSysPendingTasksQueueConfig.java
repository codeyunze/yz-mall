package com.yz.mall.sys;

/**
 * 待办任务 RocketMQ 消息队列配置抽象类
 *
 * 说明：
 * - 原实现基于 RabbitMQ（Exchange + Queue + Binding）
 * - 当前实现切换为 RocketMQ，不再需要在代码中声明 Exchange / Queue / Binding Bean
 * - 在 RocketMQ 中，使用 Topic + Tag 的形式区分不同的待办任务
 *
 * @author yunze
 * @date 2025/1/21 18:42
 */
public abstract class AbstractSysPendingTasksQueueConfig {

    /**
     * 待办任务统一 Topic 名称
     */
    public static final String TOPIC_NAME = "sys_task_topic";

}
