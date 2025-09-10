package com.yz.mall.demo.rkmq;

import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


/**
 * @author yunze
 * @since 2025/9/9 21:45
 */
@Component
public class MallProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void send(String msg, String type) {
        // rocketMQTemplate.convertAndSend("mall-demo-topic", msg);
        Message<String> message = MessageBuilder.withPayload(msg)
                // .setHeader(RocketMQHeaders.TAGS, "PAY")
                .build();
        rocketMQTemplate.convertAndSend("mall-demo-topic:" + type, msg);
    }

}
