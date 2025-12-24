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
    Result<?> notLoginExceptionHandle(NotLoginException nle) {
        log.warn(nle.getMessage());
        // 判断场景值，定制化异常信息
        String message = "";
        if (nle.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "未能读取到有效 token";
            return new Result<>(CodeEnum.ERROR_TOKEN_NULL.get(), null, CodeEnum.ERROR_TOKEN_NULL.getMsg());
        } else if (nle.getType().equals(NotLoginException.INVALID_TOKEN)) {
            message = "token 无效";
            return new Result<>(CodeEnum.ERROR_TOKEN_ILLEGAL.get(), null, CodeEnum.ERROR_TOKEN_ILLEGAL.getMsg());
        } else if (nle.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "token 已过期";
            return new Result<>(CodeEnum.ERROR_TOKEN_EXPIRED.get(), null, CodeEnum.ERROR_TOKEN_EXPIRED.getMsg());
        } else if (nle.getType().equals(NotLoginException.BE_REPLACED)) {
            message = "token 已被顶下线";
            return new Result<>(CodeEnum.ERROR_SQUEEZING_LINE.get(), null, CodeEnum.ERROR_SQUEEZING_LINE.getMsg());
        } else if (nle.getType().equals(NotLoginException.KICK_OUT)) {
            message = "token 已被踢下线";
            return new Result<>(CodeEnum.ERROR_ELIMINATE.get(), null, CodeEnum.ERROR_ELIMINATE.getMsg());
        } else if (nle.getType().equals(NotLoginException.TOKEN_FREEZE)) {
            message = "token 已被冻结";
            return new Result<>(CodeEnum.ERROR_TOKEN_ILLEGAL.get(), null, message);
        } else if (nle.getType().equals(NotLoginException.NO_PREFIX)) {
            message = "未按照指定前缀提交 token";
            return new Result<>(CodeEnum.ERROR_TOKEN_ILLEGAL.get(), null, message);
        } else {
            message = "当前会话未登录";
            return new Result<>(CodeEnum.ERROR_TOKEN_NULL.get(), null, message);
        }
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
