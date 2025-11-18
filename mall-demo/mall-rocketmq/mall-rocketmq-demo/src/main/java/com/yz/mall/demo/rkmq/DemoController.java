package com.yz.mall.demo.rkmq;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yunze
 * @since 2025/9/9 21:48
 */
@RequestMapping
@RestController
public class DemoController {

    private final MallProducer mallProducer;

    public DemoController(MallProducer mallProducer) {
        this.mallProducer = mallProducer;
    }

    @RequestMapping("/send")
    public String send(String msg, String type) {
        mallProducer.send(msg, type);
        return "发送成功：" + msg;
    }
}
