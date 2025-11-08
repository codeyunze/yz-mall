package com.yz.mall.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author yunze
 * @since 2025/11/7 12:33
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RequestHeaderMdcInterceptor mdcInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mdcInterceptor).addPathPatterns("/**");
    }
}
