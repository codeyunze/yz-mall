package com.yz.mall.sys.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.yz.mall.sys.feign.ExtendSysTestFeign;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author yunze
 * @date 2025/8/16 星期六 11:28
 */
@Slf4j
@Service
public class ExtendSysTestServiceImpl {

    private final ExtendSysTestFeign extendSysTestFeign;

    public ExtendSysTestServiceImpl(ExtendSysTestFeign extendSysTestFeign) {
        this.extendSysTestFeign = extendSysTestFeign;
    }

    /**
     * 熔断测试
     * @param id
     * @return
     */
    @CircuitBreaker(name = "testBreaker", fallbackMethod = "testFallback")
    public String testCircuitBreaker(String id) {
        if ("2".equals(id)) {
            throw new RuntimeException();
        }
        String test = extendSysTestFeign.test(id);
        return test;
    }

    /**
     * 重试测试
     * @param id
     * @return
     */
    @Retry(name = "testRetry", fallbackMethod = "testFallback")
    public String testRetry(String id) {
        log.info("id: {}, 调用: {}", id, LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_MS_PATTERN));
        if ("2".equals(id)) {
            throw new RuntimeException();
        }
        String test = extendSysTestFeign.test(id);
        return test;
    }

    /**
     * 限流测试
     * @param id
     * @return
     */
    @RateLimiter(name = "a", fallbackMethod = "testFallback")
    public String testRateLimiter(String id) {
        log.info("id: {}, 调用: {}", id, LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_MS_PATTERN));
        String test = extendSysTestFeign.test(id);
        return test;
    }

    /**
     * 服务降级
     * @param id
     * @param e
     * @return
     */
    public String testFallback(String id, Exception e) {
        log.error("触发降级");
        return "Fallback response for " + id;
    }
}
