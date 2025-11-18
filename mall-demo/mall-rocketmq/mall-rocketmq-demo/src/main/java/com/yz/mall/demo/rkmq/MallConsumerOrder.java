package com.yz.mall.demo.rkmq;

import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author yunze
 * @since 2025/9/9 22:49
 */
@Component
@RocketMQMessageListener(topic = "mall-demo-topic"
        , consumerGroup = "mall-order-consumer"
        , selectorType = SelectorType.TAG
        , selectorExpression = "ORDER"
        , messageModel = MessageModel.CLUSTERING)
public class MallConsumerOrder implements RocketMQListener<String> {

    @Override
    public void onMessage(String s) {
        System.err.println("消费下单消息：" + s);
    }
}
