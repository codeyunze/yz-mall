package com.yz.mall.demo.jaeger.a.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 调用 mall-jaeger-b 的 Feign 客户端。
 */
@FeignClient(name = "mall-jaeger-b", url = "${jaeger.b.url:http://127.0.0.1:26102}")
public interface JaegerBClient {

    /**
     * 调用 B 的问候接口。
     *
     * @param name 名称
     * @return B 返回体
     */
    @GetMapping("/b/hello")
    Map<String, Object> hello(@RequestParam("name") String name);
}
