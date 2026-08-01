package com.yz.mall.demo.kafka;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/kafka")
public class DemoController {

    private final OrderMessageProducer orderMessageProducer;

    public DemoController(OrderMessageProducer orderMessageProducer) {
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
