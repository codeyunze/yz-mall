package com.yz.mall.oms.controller;


import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.Result;
import com.yz.unqid.service.InternalUnqidService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 订单信息表(OmsOrder)表控制层
 *
 * @author yunze
 * @since 2024-06-18 12:49:54
 */
@Slf4j
@RestController
@RequestMapping("oms/order")
public class CircuitBreakerController extends ApiController {


    /**
     * 测试
     */
    @CircuitBreaker(name = "OmsOrderController", fallbackMethod = "omsCircuitFallback")
    @RequestMapping("test")
    public Result<String> test() {
        log.info("生成序列号");
        String serialNumber = internalUnqidService.generateSerialNumber("ABC241105", 6);
        log.info("serialNumber:{}", serialNumber);
        return success(serialNumber);
    }

    @Resource
    private InternalUnqidService internalUnqidService;

    /**
     * omsCircuitFallback就是服务降级后的兜底处理方法
     *
     * @param t
     * @return
     */
    public Result<String> omsCircuitFallback(Throwable t) {
        // 这里是容错处理逻辑，返回备用结果
        return failed("omsCircuitFallback，系统繁忙，请稍后再试-----/(ㄒoㄒ)/~~");
    }
}

