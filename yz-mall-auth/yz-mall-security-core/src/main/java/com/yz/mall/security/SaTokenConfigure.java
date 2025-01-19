package com.yz.mall.security;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author yunze
 * @date 2024/11/15 星期五 21:44
 */
@Slf4j
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    // 注册 Sa-Token 拦截器，打开注解式鉴权功能
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor(handle -> {
            log.warn("进入SaInterceptor拦截: {}", this.getClass());
            SaRouter.match("/**")
                    // "/beat", "/idleBeat", "/kill", "/run", "/log"为xxljob所需接口
                    .notMatch("/internal/**", "/sys/test/*", "/beat", "/idleBeat", "/kill", "/run", "/log")
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}

