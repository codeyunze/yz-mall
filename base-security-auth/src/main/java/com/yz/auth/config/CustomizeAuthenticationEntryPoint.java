package com.yz.auth.config;

import com.alibaba.fastjson.JSON;
import com.yz.common.enums.CodeEnum;
import com.yz.common.vo.Result;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 匿名用户访问无权限资源时的异常处理（跳转到登录页）
 */
@Configuration
public class CustomizeAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Result result = new Result(CodeEnum.AUTHENTICATION_ERROR.get(), null,"用户未登录");
        response.setContentType("text/json;charset=utf-8");
        // response.getWriter().write(JSON.toJSONString(result));
        response.sendRedirect("http://localhost:20002/login.html");
    }
}
