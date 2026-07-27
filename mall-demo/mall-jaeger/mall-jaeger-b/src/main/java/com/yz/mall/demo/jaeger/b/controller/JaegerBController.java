package com.yz.mall.demo.jaeger.b.controller;

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
 * 服务 B 对外接口，供 A 通过 Feign 调用。
 */
@RestController
@RequestMapping("/b")
public class JaegerBController {

    private static final Logger log = LoggerFactory.getLogger(JaegerBController.class);

    private final Tracer tracer;

    public JaegerBController(Tracer tracer) {
        this.tracer = tracer;
    }

    /**
     * 简单问候接口，返回本服务处理结果与当前 TraceId。
     *
     * @param name 名称
     * @return 响应体
     */
    @GetMapping("/hello")
    public Map<String, Object> hello(@RequestParam(defaultValue = "jaeger") String name) {
        String traceId = currentTraceId();
        log.info("mall-jaeger-b handle hello, name={}, traceId={}", name, traceId);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("service", "mall-jaeger-b");
        body.put("message", "hello " + name + " from B");
        body.put("traceId", traceId);
        return body;
    }

    private String currentTraceId() {
        if (tracer.currentSpan() == null || tracer.currentSpan().context() == null) {
            return "";
        }
        return tracer.currentSpan().context().traceId();
    }
}
