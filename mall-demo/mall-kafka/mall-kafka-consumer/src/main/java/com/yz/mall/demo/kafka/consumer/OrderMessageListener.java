package com.yz.mall.demo.kafka.consumer;

import com.yz.mall.demo.kafka.common.KafkaTopics;
import com.yz.mall.demo.kafka.common.OrderEventMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 使用 @KafkaListener 消费订单事件。
 */
@Slf4j
@Component
public class OrderMessageListener {

    @KafkaListener(
            topics = KafkaTopics.ORDER_TOPIC,
            groupId = KafkaTopics.ORDER_CONSUMER_GROUP)
    public void onMessage(OrderEventMessage message) {
        log.info("[mall-kafka-consumer] 收到订单事件: {}", message);
    }
}
