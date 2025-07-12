package com.yz.mall.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 自定义全局网关过滤器
 * @author yunze
 * @date 2025/1/2 12:42
 */
@Slf4j
@Component
public class CustomGatewayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("自定义网关过滤器...");
        // 真实IP地址
        exchange.getRequest().mutate().header("X-Real-IP", Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getHostString());
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
