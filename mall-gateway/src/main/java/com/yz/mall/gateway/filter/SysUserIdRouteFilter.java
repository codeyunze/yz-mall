package com.yz.mall.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * mall-sys 按请求头 userId 灰度转发。
 * <p>
 * 仅作用于路由 id 为 {@code mall-sys} 的请求：
 * <ul>
 * <li>无 userId 或非法值：沿用默认 {@code lb://mall-sys}</li>
 * <li>userId &gt;= 10：转发至 {@code lb://mall-sys-a}</li>
 * <li>userId &lt; 10：转发至 {@code lb://mall-sys-b}</li>
 * </ul>
 */
@Slf4j
// @Component
public class SysUserIdRouteFilter implements GlobalFilter, Ordered {

    private static final String MALL_SYS_ROUTE_ID = "mall-sys";
    private static final String USER_ID_HEADER = "userId";
    private static final String MALL_SYS_A = "mall-sys-a";
    private static final String MALL_SYS_B = "mall-sys-b";
    private static final int USER_ID_SPLIT_THRESHOLD = 10;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        if (route == null || !MALL_SYS_ROUTE_ID.equals(route.getId())) {
            return chain.filter(exchange);
        }
        String userIdHeader = exchange.getRequest().getHeaders().getFirst(USER_ID_HEADER);
        if (userIdHeader == null || userIdHeader.isBlank()) {
            return chain.filter(exchange);
        }
        Long userId = parseUserId(userIdHeader);
        if (userId == null) {
            log.warn("mall-sys 路由 userId 请求头非法，沿用默认 mall-sys: {}", userIdHeader);
            return chain.filter(exchange);
        }
        String targetService = userId >= USER_ID_SPLIT_THRESHOLD ? MALL_SYS_A : MALL_SYS_B;
        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, URI.create("lb://" + targetService));
        log.debug("mall-sys 按 userId={} 转发至 {}", userId, targetService);
        return chain.filter(exchange);
    }

    /**
     * 解析 userId 请求头为 Long，非法格式返回 null。
     *
     * @param userIdHeader 请求头原始值
     */
    private Long parseUserId(String userIdHeader) {
        try {
            return Long.parseLong(userIdHeader.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Override
    public int getOrder() {
        return ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER - 50;
    }
}
