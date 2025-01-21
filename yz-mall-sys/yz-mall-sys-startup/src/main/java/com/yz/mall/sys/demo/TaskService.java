package com.yz.mall.sys.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * @author yunze
 * @date 2025/1/20 14:49
 */
@Slf4j
@Service
public class TaskService {

    private final RabbitTemplate rabbitTemplate;

    public TaskService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String start(String taskCode) {
        String routingKey = taskCode.toLowerCase().replace(":", "_");
        // 操作数据库
        log.info("1-操作待办数据库，调整状态为开始");

        // 发送mq消息
        log.info("2-根据任务标识发送对应开始消息");
        rabbitTemplate.convertAndSend(SysPendingTaskQueueConfig.EXCHANGE_NAME, routingKey + "_start_key", "任务开始消息");
        return "taskId";
    }

    public void end(String taskCode, String taskId) {
        String routingKey = taskCode.toLowerCase().replace(":", "_");
        // 操作数据库
        log.info("1-操作待办数据库，调整状态为结束");

        // 发送mq消息
        log.info("2-根据任务标识发送对应结束消息");
        rabbitTemplate.convertAndSend(SysPendingTaskQueueConfig.EXCHANGE_NAME, routingKey + "_end_key", taskId);
    }
}
