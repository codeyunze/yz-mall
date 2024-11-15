package com.yz.mall.gateway;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.yz.tools.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * [Sa-Token 权限认证] 配置类
 *
 * @author yunze
 * @date 2024/11/15 16:11
 */
@Configuration
public class SaTokenConfigure {
    // 注册 Sa-Token全局过滤器
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")    /* 拦截全部path */
                // 开放地址
                .addExclude("/favicon.ico")
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 登录校验 -- 拦截所有路由，并排除/user/doLogin 用于开放登录
                    // SaRouter.notMatch("/auth/isLogin").match("/**", "/auth/login", r -> StpUtil.checkLogin());
                    SaRouter.match("/**").notMatch("/auth/login", "/auth/isLogin", "/auth/refreshToken/**").check(r -> StpUtil.checkLogin());

                    // 权限认证 -- 不同模块, 校验不同权限
                    // SaRouter.match("/auth/**").notMatch("/auth/login", "/auth/isLogin").check(r -> StpUtil.checkPermission("admin"));
                    SaRouter.match("/unqid/**", r -> StpUtil.checkPermission("unqid"));
                    SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
                    SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
                    SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
                    SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));

                    // 更多匹配 ...  */
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> {
                    return Result.error(e.getMessage());
                }).setBeforeAuth(r -> {
                    SaHolder.getResponse().setHeader("Content-Type", "application/json; charset=utf-8");
                })
                ;
    }
}

