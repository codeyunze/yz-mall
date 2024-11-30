package com.yz.mall.security;

import cn.dev33.satoken.exception.NotPermissionException;
import com.yz.advice.exception.BusinessException;
import com.yz.advice.exception.DataNotExistException;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 全局权限异常捕获
 *
 * @author yunze
 * @date 2024/11/30 星期六 22:57
 */
@Order(1)
@RestControllerAdvice
public class OverallPermissionExceptionHandle {

    /**
     * 权限异常问题处理
     */
    @ExceptionHandler(NotPermissionException.class)
    Result<?> notPermissionExceptionHandle(NotPermissionException e) {
        return new Result<>(CodeEnum.AUTHENTICATION_ERROR.get(), null, e.getMessage());
    }

}
