package com.yz.mall.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 登录认证过滤器
 * @author yunze
 * @date 2024/8/5 23:25
 */
@Slf4j
@Order(99)
@Component
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("99-登录认证过滤器");
        String authorization = ((HttpServletRequest) servletRequest).getHeader("Authorization");

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
