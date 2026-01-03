package com.yz.mall.demo.rkmq;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


/**
 * @author yunze
 * @since 2025/9/9 21:45
 */
@Slf4j
@Component
public class MallProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void send(String msg, String type) {
        String orderId = IdUtil.getSnowflakeNextIdStr();
        log.info("订单号：{}", orderId);
        // rocketMQTemplate.convertAndSend("mall-demo-topic", msg);
        Message<String> message = MessageBuilder.withPayload(msg)
                // .setHeader(RocketMQHeaders.TAGS, "PAY")
                .setHeader(RocketMQHeaders.KEYS, orderId)
                .build();
        // rocketMQTemplate.convertAndSend("mall-demo-topic:" + type, msg);

        SendResult result = rocketMQTemplate.syncSend("mall-demo-topic:" + type, message);
        log.info("发送结果：{}", JSONObject.toJSONString(result));
    }

}
