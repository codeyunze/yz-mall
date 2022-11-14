package com.yz.auth.config;

import com.alibaba.fastjson.JSON;
import com.yz.common.enums.CodeEnum;
import com.yz.common.vo.Result;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class CustomizeAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        Result result = null;
        if (e instanceof AccountExpiredException) {
            // 账号过期
            result = new Result(CodeEnum.AUTHENTICATION_ERROR.get(), null, "账号过期");
        } else if (e instanceof BadCredentialsException) {
            // 密码错误
            result = new Result(CodeEnum.AUTHENTICATION_ERROR.get(), null, "密码错误");
        } else if (e instanceof CredentialsExpiredException) {
            // 密码过期
            result = new Result(CodeEnum.AUTHENTICATION_ERROR.get(), null, "密码过期");
        } else if (e instanceof DisabledException) {
            // 账号不可用
            result = new Result(CodeEnum.AUTHENTICATION_ERROR.get(), null, "账号不可用");
        } else if (e instanceof LockedException) {
            // 账号锁定
            result = new Result(CodeEnum.AUTHENTICATION_ERROR.get(), null, "账号锁定");
        } else if (e instanceof InternalAuthenticationServiceException) {
            // 用户不存在
            result = new Result(CodeEnum.AUTHENTICATION_ERROR.get(), null, "用户不存在");
        } else if (e instanceof AuthenticationException) {
            // Authentication异常
            result = new Result(CodeEnum.AUTHENTICATION_ERROR.get(), null, e.getMessage());
        } else {
            // 其他错误
            result = new Result(CodeEnum.AUTHENTICATION_ERROR.get(), null, "其他错误");
        }
        // 处理编码方式，防止中文乱码的情况
        response.setContentType("text/json;charset=utf-8");
        // 塞到HttpServletResponse中返回给前台
        response.getWriter().write(JSON.toJSONString(result));
    }
}
