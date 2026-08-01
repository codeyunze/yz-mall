package com.yz.mall.auth.filter;

import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 登录认证过滤器
 * @author yunze
 * @date 2024/8/5 23:25
 */
@Slf4j
@Order(0)
@Component
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if ("/actuator/prometheus".equals(((RequestFacade) servletRequest).getRequestURI())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String authorization = ((HttpServletRequest) servletRequest).getHeader("Authorization");
        log.info("99-登录认证过滤器: {}", authorization);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
