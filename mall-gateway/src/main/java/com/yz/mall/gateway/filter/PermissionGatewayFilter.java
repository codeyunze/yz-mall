package com.yz.mall.gateway.filter;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.gateway.service.GatewayPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
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
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // 在认证过滤器之后执行
public class PermissionGatewayFilter implements GlobalFilter {

    @Autowired
    private GatewayPermissionService gatewayPermissionService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求路径
        String path = exchange.getRequest().getPath().value();
        
        // 如果用户已登录，进行权限校验
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
        
        return chain.filter(exchange);
    }
}

