package com.yz.mall.demo.kafka;

import com.yz.mall.demo.kafka.common.KafkaTopics;
import com.yz.mall.demo.kafka.common.OrderEventMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderMessageListener {

    @KafkaListener(topics = KafkaTopics.ORDER_TOPIC, groupId = KafkaTopics.DEMO_CONSUMER_GROUP)
    public void onMessage(OrderEventMessage message) {
        log.info("[mall-kafka-demo] 收到订单事件: {}", message);
    }
}
