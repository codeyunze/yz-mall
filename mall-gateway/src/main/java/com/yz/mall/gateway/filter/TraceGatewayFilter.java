package com.yz.mall.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.apache.skywalking.apm.toolkit.webflux.WebFluxSkyWalkingOperators;
import org.apache.skywalking.apm.toolkit.webflux.WebFluxSkyWalkingTraceContext;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 网关全局过滤器：补充客户端 IP，并在有 SkyWalking 上下文时回写 {@code x-trace-id}。
 * <p>
 * 跨服务链路由 Agent（SW8）负责，不再自建雪花号作为权威 TraceId；
 * 日志 TID 由 Agent + TraceIdMDCPatternLogbackLayout / GRPCLogClientAppender 注入。
 *
 * @author yunze
 * @date 2025/1/2 12:42
 */
@Slf4j
@Component
public class TraceGatewayFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "x-trace-id";
    private static final String REAL_IP_HEADER = "x-real-ip";
    private static final String CLIENT_IP = "client_ip";
    private static final String SW_NA = "N/A";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder builder = request.mutate();

        String realIp = request.getHeaders().getFirst(REAL_IP_HEADER);
        if (realIp == null || realIp.isEmpty()) {
            realIp = Objects.requireNonNull(request.getRemoteAddress()).getHostString();
            builder.header(REAL_IP_HEADER, realIp);
        }

        // 有 SkyWalking TID 时写入请求头，便于无 Agent 的下游兜底透传；无则保留客户端原 header
        String skyWalkingTid = resolveSkyWalkingTraceId(exchange);
        if (skyWalkingTid != null) {
            builder.header(TRACE_ID_HEADER, skyWalkingTid);
        }

        ServerWebExchange modifiedExchange = exchange.mutate().request(builder.build()).build();
        final String clientIp = realIp;
        putClientIp(clientIp);

        return chain.filter(modifiedExchange)
                .doOnEach(signal -> WebFluxSkyWalkingOperators.continueTracing(modifiedExchange, () -> putClientIp(clientIp)))
                .doOnSuccess(v -> writeTraceHeader(modifiedExchange))
                .doOnError(e -> writeTraceHeader(modifiedExchange))
                .doFinally(signalType -> MDC.remove(CLIENT_IP));
    }

    private static String resolveSkyWalkingTraceId(ServerWebExchange exchange) {
        String tid = TraceContext.traceId();
        if (tid != null && !tid.isEmpty() && !SW_NA.equals(tid)) {
            return tid;
        }
        tid = WebFluxSkyWalkingTraceContext.traceId(exchange);
        return (tid == null || tid.isEmpty() || SW_NA.equals(tid)) ? null : tid;
    }

    private static void putClientIp(String realIp) {
        try {
            if (realIp != null && !realIp.isEmpty()) {
                MDC.put(CLIENT_IP, realIp);
            }
        } catch (Exception e) {
            log.warn("设置 client_ip MDC 失败", e);
        }
    }

    private static void writeTraceHeader(ServerWebExchange exchange) {
        try {
            String tid = resolveSkyWalkingTraceId(exchange);
            if (tid != null && !exchange.getResponse().isCommitted()) {
                exchange.getResponse().getHeaders().set(TRACE_ID_HEADER, tid);
            }
        } catch (Exception e) {
            log.warn("回写 trace 响应头失败", e);
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
