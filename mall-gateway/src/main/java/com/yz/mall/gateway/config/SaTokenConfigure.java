package com.yz.mall.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @date 2025/12/11 星期四 20:17
 */
@Configuration
public class SaTokenConfigure {
    /**
     * 注册 [Sa-Token全局过滤器]
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 指定 [拦截路由]
                .addInclude("/**")    /* 拦截所有path */
                // 指定 [放行路由]
                .addExclude("/authentication/login", "/authentication/logout")
                // 指定[认证函数]: 每次请求执行
                .setAuth(obj -> {
                    System.out.println("---------- sa全局认证");

                    SaRouter.match("/**", StpUtil::checkLogin);

                    // SaRouter.match("/test/test", () -> StpUtil.checkLogin());
                })
                // 指定[异常处理函数]：每次[认证函数]发生异常时执行此函数
                .setError(e -> {
                    System.out.println("---------- sa全局异常 ");
                    return SaResult.error(e.getMessage());
                })
                ;
    }
}
