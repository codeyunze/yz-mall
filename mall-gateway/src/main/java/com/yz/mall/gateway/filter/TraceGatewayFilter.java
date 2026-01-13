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
import reactor.util.context.Context;

import java.util.Objects;

/**
 * 自定义全局网关过滤器
 * <p>
 * 设置 trace-id 和 client-ip 到请求头，并设置到 MDC 中用于日志追踪
 *
 * @author yunze
 * @date 2025/1/2 12:42
 */
@Slf4j
@Component
public class TraceGatewayFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "x-trace-id";
    private static final String REAL_IP_HEADER = "x-real-ip";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取原始请求
        ServerHttpRequest request = exchange.getRequest();

        // 构建新的请求构建器，基于现有请求
        ServerHttpRequest.Builder builder = request.mutate();

        // 获取或生成 traceId
        String traceIdValue = request.getHeaders().getFirst(TRACE_ID_HEADER);
        if (traceIdValue == null || traceIdValue.isEmpty()) {
            traceIdValue = IdUtil.getSnowflakeNextIdStr();
            builder.header(TRACE_ID_HEADER, traceIdValue);
        }

        // 获取真实IP地址
        String realIpValue = request.getHeaders().getFirst(REAL_IP_HEADER);
        if (realIpValue == null || realIpValue.isEmpty()) {
            realIpValue = Objects.requireNonNull(request.getRemoteAddress()).getHostString();
            builder.header(REAL_IP_HEADER, realIpValue);
        }

        // 构建新的请求对象
        ServerHttpRequest modifiedRequest = builder.build();
        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

        // 使用 final 变量，以便在 lambda 中使用
        final String traceId = traceIdValue;
        final String realIp = realIpValue;

        // 立即设置 MDC，确保后续过滤器可以获取到 MDC 值
        try {
            MDC.put("trace_id", traceId);
            MDC.put("client_ip", realIp);
        } catch (Exception e) {
            log.warn("设置 MDC 失败", e);
        }

        // 在响应式链中保持 MDC，并在完成后清理
        return chain.filter(modifiedExchange)
                .doOnEach(signal -> {
                    // 在每个信号时确保 MDC 已设置（防止线程切换导致 MDC 丢失）
                    try {
                        MDC.put("trace_id", traceId);
                        MDC.put("client_ip", realIp);
                    } catch (Exception e) {
                        log.warn("设置 MDC 失败", e);
                    }
                })
                .doFinally(signalType -> {
                    // 无论成功还是失败，都清理 MDC
                    try {
                        MDC.clear();
                    } catch (Exception e) {
                        log.warn("清理MDC失败", e);
                    }
                })
                .contextWrite(Context.of("trace_id", traceId, "client_ip", realIp));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
