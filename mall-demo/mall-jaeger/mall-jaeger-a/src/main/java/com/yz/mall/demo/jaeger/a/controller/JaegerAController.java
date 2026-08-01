package com.yz.mall.demo.jaeger.a.controller;

import com.yz.mall.demo.jaeger.a.feign.JaegerBClient;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 服务 A 入口：通过 Feign 调用 B，形成跨服务 Trace。
 */
@RestController
@RequestMapping("/a")
public class JaegerAController {

    private static final Logger log = LoggerFactory.getLogger(JaegerAController.class);

    private final JaegerBClient jaegerBClient;
    private final Tracer tracer;

    public JaegerAController(JaegerBClient jaegerBClient, Tracer tracer) {
        this.jaegerBClient = jaegerBClient;
        this.tracer = tracer;
    }

    /**
     * 调用链路：HTTP → A → Feign → B，可在 Jaeger UI 查看完整 Trace。
     *
     * @param name 名称
     * @return 聚合 A/B 结果
     */
    @GetMapping("/hello")
    public Map<String, Object> hello(@RequestParam(defaultValue = "jaeger") String name) {
        String traceId = currentTraceId();
        log.info("mall-jaeger-a handle hello, name={}, traceId={}", name, traceId);
        Map<String, Object> fromB = jaegerBClient.hello(name);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("service", "mall-jaeger-a");
        body.put("message", "hello " + name + " from A");
        body.put("traceId", traceId);
        body.put("downstream", fromB);
        return body;
    }

    private String currentTraceId() {
        if (tracer.currentSpan() == null || tracer.currentSpan().context() == null) {
            return "";
        }
        return tracer.currentSpan().context().traceId();
    }
}
