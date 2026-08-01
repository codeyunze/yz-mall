package com.yz.mall.auth;

import cn.dev33.satoken.interceptor.SaInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 拦截器配置
 * <p>
 * 注册 Sa-Token 拦截器，打开注解式鉴权功能
 * 支持 @SaCheckPermission、@SaCheckLogin、@SaCheckRole 等注解
 *
 * @author yunze
 * @date 2024/11/15 星期五 21:44
 */
@Slf4j
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    
    /**
     * 注册 Sa-Token 拦截器，打开注解式鉴权功能
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        // SaInterceptor 会自动处理 @SaCheckPermission、@SaCheckLogin、@SaCheckRole 等注解
        // 注意：不传入 handle 函数，只处理注解式鉴权，避免在上下文未初始化时执行路由匹配
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/druid/**", "/actuator/**", "/error/**");
    }
}

