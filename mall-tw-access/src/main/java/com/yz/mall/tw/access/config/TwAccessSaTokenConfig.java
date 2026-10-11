package com.yz.mall.tw.access.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token：放行 EMQX 回调与健康检查。
 */
@Configuration
public class TwAccessSaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> SaRouter.match("/**")
                        .notMatch("/emqx/**")
                        .notMatch("/actuator/**")
                        .notMatch("/error")
                        .check(r -> StpUtil.checkLogin())))
                .addPathPatterns("/**");
    }
}
