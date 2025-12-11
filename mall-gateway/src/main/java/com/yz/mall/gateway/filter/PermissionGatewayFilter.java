package com.yz.mall.gateway.filter;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.gateway.config.GatewayAuthProperties;
import com.yz.mall.gateway.service.GatewayPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关权限校验过滤器
 * 在认证通过后，根据接口路径进行权限校验
 * 
 * 注意：由于网关层无法直接获取 Controller 上的 @SaCheckPermission 注解信息，
 * 需要通过路径匹配或配置来获取接口所需的权限标识
 *
 * @author yunze
 * @since 2025-12-05
 */
@Slf4j
// @Component
// @Order(101) // 在认证过滤器之后执行，使用正数确保在 SaToken 响应式过滤器之后
public class PermissionGatewayFilter implements GlobalFilter {

    @Autowired
    private GatewayPermissionService gatewayPermissionService;

    @Autowired
    private GatewayAuthProperties gatewayAuthProperties;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求路径
        String path = exchange.getRequest().getPath().value();
        
        // 检查是否在白名单中，白名单路径不需要权限校验
        if (isWhiteList(path)) {
            log.debug("网关权限过滤器 - 白名单路径，跳过权限校验: {}", path);
            return chain.filter(exchange);
        }
        
        // 检查是否启用认证（如果认证未启用，也不进行权限校验）
        if (!gatewayAuthProperties.getEnabled()) {
            log.debug("网关权限过滤器 - 认证已禁用，跳过权限校验: {}", path);
            return chain.filter(exchange);
        }
        
        // 检查用户是否已登录，如果已登录则进行权限校验
        try {
            SaReactorSyncHolder.setContext(exchange);

            if (StpUtil.isLogin()) {
                // 获取接口所需的权限标识
                String requiredPermission = gatewayPermissionService.getRequiredPermission(path);
                
                if (StringUtils.hasText(requiredPermission)) {
                    try {
                        // 检查权限
                        StpUtil.checkPermission(requiredPermission);
                        log.debug("网关权限校验通过 - 路径: {}, 权限: {}", path, requiredPermission);
                    } catch (NotPermissionException e) {
                        // 权限校验失败
                        log.warn("网关权限校验失败 - 路径: {}, 权限: {}, 用户: {}", 
                                path, requiredPermission, StpUtil.getLoginIdDefaultNull());
                        
                        ServerHttpResponse response = exchange.getResponse();
                        response.setStatusCode(HttpStatus.FORBIDDEN);
                        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                        
                        String errorResponse = String.format(
                            "{\"code\":403,\"msg\":\"无权限访问该接口: %s\",\"data\":null}",
                            requiredPermission
                        );
                        
                        return response.writeWith(
                            Mono.just(response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8)))
                        );
                    } catch (Exception e) {
                        // 其他异常，记录日志但继续执行
                        log.error("网关权限校验异常 - 路径: {}, 权限: {}", path, requiredPermission, e);
                    }
                } else {
                    // 如果路径权限映射中没有配置，说明该接口可能不需要权限校验
                    // 或者权限校验由业务服务处理（如果业务服务仍保留权限校验逻辑）
                    log.debug("网关权限校验跳过 - 路径: {} (未配置权限要求)", path);
                }
            }
        } catch (cn.dev33.satoken.exception.SaTokenContextException e) {
            // SaToken 上下文未初始化异常（在响应式环境中可能发生）
            // 对于白名单路径，应该已经提前返回，不应该执行到这里
            // 如果执行到这里，说明可能是配置问题，记录警告并继续执行
            log.warn("网关权限过滤器 - SaToken上下文未初始化，路径: {}, 错误: {}", path, e.getMessage());
            // 继续执行，让下游服务处理
        } catch (Exception e) {
            // 其他异常，记录日志但继续执行
            log.error("网关权限过滤器 - 检查登录状态异常，路径: {}", path, e);
        } finally {
            SaReactorSyncHolder.clearContext();
        }
        
        return chain.filter(exchange);
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
}

