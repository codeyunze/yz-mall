package com.yz.mall.sys.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author yunze
 * @date 2025/1/20 15:16
 */
@Slf4j
@Component
public abstract class BaseService {

    abstract String setMessageQueue();

    abstract void startTask();

    abstract void endTask(String taskId);

    // @RabbitListener(queues = "pms_product_publish_start")
    public void start(Message message) {
        // 接收到消息
        log.info("3-接收到消息后，准备执行业务的开始待办逻辑");
        startTask();
    }

    // @RabbitListener(queues = "pms_product_publish_end")
    public void end(Message message) {
        // 接收到消息
        log.info("3-接收到消息后，准备执行业务的结束待办逻辑");
        endTask(message.toString());
    }

}
