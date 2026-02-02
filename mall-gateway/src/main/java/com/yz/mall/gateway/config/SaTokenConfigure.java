package com.yz.mall.gateway.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 网关过滤器配置
 * <p>
 * 注意：SaReactorFilter 可能已经实现了 Ordered 接口，但其 order 值可能是固定的
 * 如果执行顺序不对，可以调整 TokenParameterFilter 的 order 值（当前为 -300）
 * 确保 TokenParameterFilter 在 SaReactorFilter 之前执行
 *
 * @author yunze
 * @date 2025/12/11 星期四 20:17
 */
@Slf4j
@Configuration
public class SaTokenConfigure {
    /**
     * 注册 [Sa-Token全局过滤器]
     * <p>
     * SaReactorFilter 的 order 值可能是固定的（可能小于 -300）
     * 如果 TokenParameterFilter（order=-300）仍然在 SaReactorFilter 之后执行，
     * 可以尝试将 TokenParameterFilter 的 order 调整为更小的值（如 -400、-500）
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 指定 [拦截路由]
                .addInclude("/**")    /* 拦截所有path */
                // 指定 [放行路由]
                .addExclude("/authentication/login", "/authentication/logout", "/authentication/captcha")
                // 指定[认证函数]: 每次请求执行
                .setAuth(obj -> {
                    SaRouter.match("/**", StpUtil::checkLogin);
                })
                // 指定[异常处理函数]：每次[认证函数]发生异常时执行此函数
                .setError(e -> {
                    log.error(e.getMessage());
                    if (e instanceof NotLoginException) {
                        return new SaResult(((NotLoginException) e).getCode(), e.getMessage(), null);
                    }
                    return SaResult.error(e.getMessage());
                });
    }
}
