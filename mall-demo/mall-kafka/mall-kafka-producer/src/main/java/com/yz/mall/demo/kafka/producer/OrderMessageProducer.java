package com.yz.mall.demo.kafka.producer;

import cn.hutool.core.util.IdUtil;
import com.yz.mall.demo.kafka.common.KafkaTopics;
import com.yz.mall.demo.kafka.common.OrderEventMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 使用 KafkaTemplate 发送订单事件。
 */
@Slf4j
@Service
public class OrderMessageProducer {

    private final KafkaTemplate<String, OrderEventMessage> kafkaTemplate;

    public OrderMessageProducer(KafkaTemplate<String, OrderEventMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public boolean send(String eventType, String payload) {
        OrderEventMessage message = new OrderEventMessage(
                IdUtil.getSnowflakeNextIdStr(),
                eventType,
                payload,
                LocalDateTime.now());
        try {
            kafkaTemplate.send(KafkaTopics.ORDER_TOPIC, message.getOrderId(), message)
                    .get(5, TimeUnit.SECONDS);
            log.info("发送 Kafka 消息成功: {}", message);
            return true;
        } catch (Exception ex) {
            log.error("发送 Kafka 消息失败: {}", message, ex);
            return false;
        }
    }
}
