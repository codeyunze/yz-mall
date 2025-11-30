package com.yz.mall.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * SaToken 相关全局异常捕获
 *
 * @author yunze
 * @date 2024/11/30 星期六 22:57
 */
@Slf4j
@Order(1)
@RestControllerAdvice
public class OverallPermissionExceptionHandle {

    /**
     * 令牌异常处理
     */
    @ExceptionHandler(NotLoginException.class)
    Result<?> notLoginExceptionHandle(NotLoginException e) {
        log.warn(e.getMessage());
        if (e.getMessage().contains("未能读取到有效")) {
            return new Result<>(CodeEnum.ERROR_TOKEN_NULL.get(), null, CodeEnum.ERROR_TOKEN_NULL.getMsg());
        }
        return new Result<>(CodeEnum.ERROR_TOKEN_ILLEGAL.get(), null, StringUtils.hasText(e.getMessage()) ? e.getMessage() : CodeEnum.ERROR_TOKEN_ILLEGAL.getMsg());
    }

    /**
     * 权限异常问题处理
     */
    @ExceptionHandler(NotPermissionException.class)
    Result<?> notPermissionExceptionHandle(NotPermissionException e) {
        log.warn(e.getMessage());
        return new Result<>(CodeEnum.AUTHENTICATION_ERROR.get(), null, CodeEnum.AUTHENTICATION_ERROR.getMsg());
    }

}
