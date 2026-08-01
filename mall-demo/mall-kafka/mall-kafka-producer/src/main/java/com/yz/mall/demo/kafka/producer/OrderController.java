package com.yz.mall.demo.kafka.producer;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 提供 HTTP 接口触发 Kafka 消息发送。
 */
@RestController
@RequestMapping("/kafka")
public class OrderController {

    private final OrderMessageProducer orderMessageProducer;

    public OrderController(OrderMessageProducer orderMessageProducer) {
        this.orderMessageProducer = orderMessageProducer;
    }

    @PostMapping("/send")
    public Map<String, Object> send(
            @RequestParam(defaultValue = "ORDER_CREATED") String eventType,
            @RequestParam(defaultValue = "demo payload") String payload) {
        boolean ok = orderMessageProducer.send(eventType, payload);
        return Map.of(
                "success", ok,
                "eventType", eventType,
                "payload", payload);
    }
}
