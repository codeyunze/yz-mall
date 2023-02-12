package com.yz.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName CheckAuthFilter
 * @Description 校验token权限
 * @Author yunze
 * @Date 2022/11/19 22:02
 * @Version 1.0
 */
@Component
@Slf4j
public class CheckAuthFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String uri = exchange.getRequest().getPath().toString();
        String firstUri = uri.split("/")[1];
        List<String> excludes = new ArrayList<>();
        excludes.add("security");
        excludes.add("v2");

        if (!excludes.contains(firstUri)) {
            String token = exchange.getRequest().getHeaders().getFirst("token");
            if (null == token) {
                log.info("token is null");
                ServerHttpResponse response = exchange.getResponse();
                response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                byte[] bytes = HttpStatus.UNAUTHORIZED.getReasonPhrase().getBytes();
                DataBuffer buffer = response.bufferFactory().wrap(bytes);
                return response.writeWith(Mono.just(buffer));
            }
            // TODO: 2022/11/19 校验token有效性
            log.info("token校验通过");
        } else {
            log.info("越过token校验");
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 过滤器执行顺序，数字越小，越先执行
        return 9;
    }
}
