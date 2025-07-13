package com.yz.mall.base.exception;

import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.UnknownHostException;
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

    private static final Logger log = LoggerFactory.getLogger(OverallExceptionHandle.class);

    /**
     * 业务异常问题处理
     */
    @ExceptionHandler(BusinessException.class)
    Result<?> businessExceptionHandle(BusinessException e) {
        return Result.error(e.getMessage());
    }

    /**
     * 业务数据不存在异常问题处理
     */
    @ExceptionHandler(DataNotExistException.class)
    Result<?> dataNotExistExceptionHandle(DataNotExistException e) {
        return Result.error(e.getMessage());
    }


    /**
     * 业务数据已经存在异常问题处理
     */
    @ExceptionHandler(DuplicateException.class)
    Result<?> duplicateExceptionHandle(DuplicateException e) {
        return new Result<>(CodeEnum.ALREADY_EXISTS_ERROR.get(), null, StringUtils.hasText(e.getMessage()) ? e.getMessage() : CodeEnum.ALREADY_EXISTS_ERROR.getMsg());
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

    /**
     * 无访问业务权限问题处理
     */
    @ExceptionHandler(AuthenticationException.class)
    Result<?> authenticationExceptionHandle(AuthenticationException e) {
        return new Result<>(CodeEnum.AUTHENTICATION_ERROR.get(), null, e.getMessage());
    }

    /**
     * 服务掉线问题处理
     */
    @ExceptionHandler(UnknownHostException.class)
    Result<?> authenticationExceptionHandle(UnknownHostException e) {
        log.error(e.getMessage(), e);
        return new Result<>(CodeEnum.SYSTEM_ERROR.get(), null, "服务异常，请稍后再试");
    }
}
