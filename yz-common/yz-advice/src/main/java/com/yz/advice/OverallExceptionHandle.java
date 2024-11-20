package com.yz.advice;

import com.yz.advice.exception.BusinessException;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 全局异常捕获
 * @author yunze
 * @date 2024/6/19 星期三 22:57
 */
@Order(0)
@RestControllerAdvice
public class OverallExceptionHandle {

    /**
     * 业务异常问题处理
     */
    @ExceptionHandler(BusinessException.class)
    Result businessExceptionHandle(BusinessException e) {
        return Result.error(e.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    Result<?> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e) {
        return new Result<>(CodeEnum.PARAMS_ERROR.get(), null, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }
}
