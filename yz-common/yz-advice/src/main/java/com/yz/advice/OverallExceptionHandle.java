package com.yz.advice;

import com.yz.advice.exception.BusinessException;
import com.yz.advice.exception.DataNotExistException;
import com.yz.advice.exception.FeignException;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;

/**
 * 全局异常捕获
 *
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
    Result<?> businessExceptionHandle(BusinessException e) {
        return Result.error(e.getMessage());
    }

    /**
     * 业务异常问题处理
     */
    @ExceptionHandler(DataNotExistException.class)
    Result<?> DataNotExistExceptionHandle(DataNotExistException e) {
        return Result.error(e.getMessage());
    }

    /**
     * 参数校验异常提示
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    Result<?> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e) {
        return new Result<>(CodeEnum.PARAMS_ERROR.get(), null, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    /**
     * SQL完整性约束异常
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    Result<?> sqlIntegrityConstraintViolationExceptionHandle(SQLIntegrityConstraintViolationException e) {
        e.printStackTrace();
        return new Result<>(CodeEnum.ALREADY_EXISTS_ERROR.get(), null, CodeEnum.ALREADY_EXISTS_ERROR.getMsg());
    }

    /**
     * Feign请求异常
     */
    @ExceptionHandler(FeignException.class)
    Result<?> feignExceptionHandle(FeignException e) {
        e.printStackTrace();
        return new Result<>(e.getCode(), null, e.getMessage());
    }
}
