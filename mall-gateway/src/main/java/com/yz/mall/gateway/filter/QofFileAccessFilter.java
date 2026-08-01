package com.yz.mall.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * qof-web FileController访问拦截过滤器
 * <p>
 * 拦截所有对 /file/* 路径的请求，拒绝直接访问qof-web的FileController接口
 * 文件操作应通过 /sys/file/* 接口进行
 *
 * @author yunze
 * @date 2025/12/21
 */
@Slf4j
@Component
public class QofFileAccessFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("网关层拦截禁止访问接口校验：{}", path);

        // 拦截 /file/* 路径的请求（排除 /sys/file/*）
        if (path.startsWith("/file/") && !path.startsWith("/sys/file/")) {
            log.warn("网关拦截：拒绝直接访问qof-web FileController接口: {}, 请使用 /sys/file/* 接口", path);
            
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            
            String errorMessage = "{\"code\":403,\"msg\":\"禁止访问接口\",\"data\":null}";
            DataBuffer buffer = response.bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8));
            
            return response.writeWith(Mono.just(buffer));
        }
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 设置较高优先级，确保在其他过滤器之前执行
        return 1;
    }
}

