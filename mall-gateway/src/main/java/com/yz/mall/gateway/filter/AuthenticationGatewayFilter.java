package com.yz.mall.gateway.filter;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.gateway.config.GatewayAuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关认证过滤器
 * 在网关层统一进行登录认证
 * 
 * 注意：权限校验由 PermissionGatewayFilter 处理
 *
 * @author yunze
 * @since 2025-12-05
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) // 最高优先级，最先执行
public class AuthenticationGatewayFilter implements GlobalFilter {

    @Autowired
    private GatewayAuthProperties gatewayAuthProperties;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求路径
        String path = exchange.getRequest().getPath().value();
        
        // 检查是否在白名单中
        if (isWhiteList(path)) {
            log.debug("网关认证过滤器 - 白名单路径，跳过认证: {}", path);
            return chain.filter(exchange);
        }
        
        // 检查是否启用认证
        if (!gatewayAuthProperties.getEnabled()) {
            log.debug("网关认证过滤器 - 认证已禁用，跳过认证: {}", path);
            return chain.filter(exchange);
        }
        
        try {
            // 登录校验
            StpUtil.checkLogin();
            
            // 获取用户ID
            Object loginId = StpUtil.getLoginIdDefaultNull();
            log.debug("网关认证过滤器 - 认证通过，路径: {}, 用户ID: {}", path, loginId);
            
            // 将用户ID添加到请求头，传递给下游服务
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(builder -> builder.header("X-User-Id", String.valueOf(loginId)))
                    .build();
            
            return chain.filter(modifiedExchange);
            
        } catch (NotLoginException e) {
            // 未登录异常
            log.warn("网关认证失败 - 未登录: {}, 路径: {}", e.getMessage(), path);
            return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED, 401, "未登录或登录已过期，请重新登录");
            
        } catch (Exception e) {
            // 其他异常
            log.error("网关认证异常 - 路径: {}", path, e);
            return buildErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR, 500, "认证服务异常: " + e.getMessage());
        }
    }
    
    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhiteList(String path) {
        if (!StringUtils.hasText(path)) {
            return false;
        }
        
        for (String whitePath : gatewayAuthProperties.getWhiteList()) {
            if (pathMatcher.match(whitePath, path)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 构建错误响应
     */
    private Mono<Void> buildErrorResponse(ServerWebExchange exchange, HttpStatus httpStatus, 
                                          int errorCode, String errorMsg) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        
        String errorResponse = String.format(
            "{\"code\":%d,\"msg\":\"%s\",\"data\":null}",
            errorCode, errorMsg
        );
        
        return response.writeWith(
            Mono.just(response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8)))
        );
    }
}

