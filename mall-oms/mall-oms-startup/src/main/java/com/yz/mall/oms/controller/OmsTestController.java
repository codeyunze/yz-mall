package com.yz.mall.oms.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.yz.mall.sys.service.impl.ExtendSysTestServiceImpl;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OMS-测试
 *
 * @author yunze
 * @date 2025/8/16 星期六 9:45
 */
@Slf4j
@RestController
@RequestMapping("oms/test")
public class OmsTestController {

    private final ExtendSysTestServiceImpl extendSysTestService;

    public OmsTestController(ExtendSysTestServiceImpl extendSysTestService) {
        this.extendSysTestService = extendSysTestService;
    }

    /**
     * 测试接口（熔断）
     */
    @SaIgnore
    @RequestMapping("testCircuitBreaker/{id}")
    public String testCircuitBreaker(@PathVariable String id) {
        log.info("熔断测试接口");
        String test = extendSysTestService.testCircuitBreaker(id);
        return test;
    }

    /**
     * 测试接口（重试）
     */
    @SaIgnore
    @RequestMapping("testRetry/{id}")
    public String testRetry(@PathVariable String id) {
        log.info("重试测试接口");
        String test = extendSysTestService.testRetry(id);
        return test;
    }

    /**
     * 测试接口（限流）
     */
    @SaIgnore
    @RateLimiter(name = "testRateLimiter")
    @RequestMapping("testRateLimiter/{id}")
    public String testRateLimiter(@PathVariable String id) {
        log.info("限流测试接口");
        String test = extendSysTestService.testRateLimiter(id);
        return test;
    }


}
