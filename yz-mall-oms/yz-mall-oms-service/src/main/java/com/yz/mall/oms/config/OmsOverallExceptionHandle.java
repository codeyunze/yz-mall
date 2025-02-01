package com.yz.mall.oms.config;

import com.yz.mall.web.common.Result;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获
 *
 * @author yunze
 * @date 2024/6/19 星期三 22:57
 */
@Order(1)
@RestControllerAdvice
public class OmsOverallExceptionHandle {

    /**
     * 拒绝请求问题处理
     */
    @ExceptionHandler(CallNotPermittedException.class)
    Result<?> callNotPermittedExceptionHandle(CallNotPermittedException e) {
        return Result.error(e.getMessage());
    }

}
