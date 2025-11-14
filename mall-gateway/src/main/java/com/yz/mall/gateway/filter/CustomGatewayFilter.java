package com.yz.mall.gateway.filter;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

/**
 * 自定义全局网关过滤器
 *
 * @author yunze
 * @date 2025/1/2 12:42
 */
@Slf4j
@Component
public class CustomGatewayFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "X-Trace-ID";
    private static final String REAL_IP_HEADER = "X-Real-IP";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("自定义网关过滤器...");
        // 获取原始请求
        ServerHttpRequest request = exchange.getRequest();

        // 构建新的请求构建器，基于现有请求
        ServerHttpRequest.Builder builder = request.mutate();

        // 判断如果header里没有X-Trace-ID参数，则为其赋值随机UUID
        if (exchange.getRequest().getHeaders().get(TRACE_ID_HEADER) == null) {
            builder.header(TRACE_ID_HEADER, IdUtil.getSnowflakeNextIdStr());
        }

        // 真实IP地址
        if (exchange.getRequest().getHeaders().get(REAL_IP_HEADER) == null) {
            builder.header(REAL_IP_HEADER, Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getHostString());
        }

        // 构建新的请求对象
        ServerHttpRequest modifiedRequest = builder.build();

        // 使用修改后的请求继续处理链
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
