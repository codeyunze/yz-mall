package com.yz.mall.web;

import com.yz.mall.web.interceptor.RequestHeaderInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author yunze
 * @since 2025/7/13 22:53
 */
@Configuration
@ComponentScan({"com.yz.mall.web"})
public class MallWebConfig implements WebMvcConfigurer {

    @Autowired
    private RequestHeaderInterceptor mdcInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mdcInterceptor).addPathPatterns("/**");
    }
}
