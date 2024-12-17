package com.yz.mall.gateway;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

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
                    SaRouter.match("/**")
                            .notMatch("/login", "/logout", "/refreshToken", "/register", "/sys/**", "/file/**")
                            .check(r -> StpUtil.checkLogin());

                    // 权限认证 -- 不同模块, 校验不同权限
                    // SaRouter.match("/auth/**").notMatch("/auth/login", "/auth/isLogin").check(r -> StpUtil.checkPermission("admin"));
                    // SaRouter.match("/unqid/**", r -> StpUtil.checkPermission("unqid"));
                    // SaRouter.match("/oms/**", r -> StpUtil.checkPermission("oms"));
                    // SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
                    // SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
                    // SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
                    // SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));

                    // 更多匹配 ...  */
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> {
                    // return SaResult.error(e.getMessage());
                    System.out.println("---------- 进入Sa-Token异常处理 -----------");
                    // 设置响应头
                    SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
                    // 使用封装的 JSON 工具类转换数据格式
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        return objectMapper.writeValueAsString(new Result<>(CodeEnum.AUTHENTICATION_ERROR.get(), null, StringUtils.hasText(e.getMessage()) ? e.getMessage() : "无效访问令牌"));
                    } catch (JsonProcessingException ex) {
                        throw new RuntimeException(ex);
                    }
                }).setBeforeAuth(r -> {
                    // SaHolder.getResponse().setHeader("Content-Type", "application/json; charset=utf-8");
                })
                ;
    }
}

