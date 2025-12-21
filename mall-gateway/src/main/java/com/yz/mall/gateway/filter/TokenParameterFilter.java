package com.yz.mall.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Token参数过滤器
 * <p>
 * 如果URL查询参数中包含token参数，则将其提取并添加到Authorization请求头中
 * 格式：Authorization: Bearer {token}
 *
 * @author yunze
 * @date 2025/12/21
 */
@Slf4j
@Component
public class TokenParameterFilter implements GlobalFilter, Ordered {

    private static final String TOKEN_PARAM = "token";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 检查请求头中是否已有 Authorization
        if (request.getHeaders().containsKey(AUTHORIZATION_HEADER)) {
            // 如果已有Authorization头，直接放行
            return chain.filter(exchange);
        }

        // 从URL查询参数中获取 token
        List<String> tokenValues = request.getQueryParams().get(TOKEN_PARAM);
        
        if (tokenValues != null && !tokenValues.isEmpty()) {
            String token = tokenValues.get(0);
            
            if (token != null && !token.trim().isEmpty()) {
                log.debug("从URL参数中提取token并添加到Authorization头: {}", token);
                
                // 构建新的请求，添加Authorization头
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                        .build();
                
                // 使用修改后的请求继续处理链
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }
        }
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 设置较高优先级，确保在认证过滤器之前执行
        return -50;
    }
}

