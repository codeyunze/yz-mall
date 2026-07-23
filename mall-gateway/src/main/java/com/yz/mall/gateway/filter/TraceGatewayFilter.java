package com.yz.mall.gateway.filter;

import cn.hutool.core.util.IdUtil;
import com.yz.mall.base.trace.SkyWalkingTraceMdc;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.apache.skywalking.apm.toolkit.webflux.WebFluxSkyWalkingOperators;
import org.apache.skywalking.apm.toolkit.webflux.WebFluxSkyWalkingTraceContext;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 网关全局过滤器：将 SkyWalking trace/span 与客户端 IP 写入请求头与 MDC。
 * <p>
 * 网关为 WebFlux 模型，异步回调里 {@link TraceContext} 常为 N/A，必须用
 * {@link WebFluxSkyWalkingTraceContext} 从 exchange 取与下游一致的 TID，避免雪花号与业务模块对不上。
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
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder builder = request.mutate();

        String realIpValue = request.getHeaders().getFirst(REAL_IP_HEADER);
        if (realIpValue == null || realIpValue.isEmpty()) {
            realIpValue = Objects.requireNonNull(request.getRemoteAddress()).getHostString();
            builder.header(REAL_IP_HEADER, realIpValue);
        }

        // 仅无 Agent 本地开发时才用 header/雪花兜底；有 Agent 时绝不把雪花写死为“权威” TID
        String headerTraceId = request.getHeaders().getFirst(TRACE_ID_HEADER);
        String skyWalkingTid = resolveSkyWalkingTraceId(exchange);
        String noAgentFallback = skyWalkingTid == null
                ? (headerTraceId != null && !headerTraceId.isEmpty() ? headerTraceId : IdUtil.getSnowflakeNextIdStr())
                : null;
        String initialTid = skyWalkingTid != null ? skyWalkingTid : noAgentFallback;
        if (initialTid != null) {
            builder.header(TRACE_ID_HEADER, initialTid);
        }

        ServerWebExchange modifiedExchange = exchange.mutate().request(builder.build()).build();
        final String fallbackTid = initialTid;
        final String realIp = realIpValue;

        bindMdc(modifiedExchange, fallbackTid, realIp);

        return chain.filter(modifiedExchange)
                .doOnEach(signal -> WebFluxSkyWalkingOperators.continueTracing(modifiedExchange,
                        () -> bindMdc(modifiedExchange, fallbackTid, realIp)))
                .doOnSuccess(v -> writeTraceHeader(modifiedExchange, fallbackTid))
                .doOnError(e -> writeTraceHeader(modifiedExchange, fallbackTid))
                .doFinally(signalType -> {
                    try {
                        SkyWalkingTraceMdc.clear();
                    } catch (Exception e) {
                        log.warn("清理MDC失败", e);
                    }
                });
    }

    /**
     * 优先线程上下文，其次 exchange 上的 WebFlux 助手（异步回调必须走这个）。
     */
    private static String resolveSkyWalkingTraceId(ServerWebExchange exchange) {
        String tid = SkyWalkingTraceMdc.currentTraceIdOrNull();
        if (tid != null) {
            return tid;
        }
        tid = WebFluxSkyWalkingTraceContext.traceId(exchange);
        return SkyWalkingTraceMdc.isUnavailable(tid) ? null : tid;
    }

    private static String resolveSkyWalkingSpanId(ServerWebExchange exchange) {
        int spanId = TraceContext.spanId();
        if (spanId >= 0) {
            return Integer.toString(spanId);
        }
        spanId = WebFluxSkyWalkingTraceContext.spanId(exchange);
        return spanId < 0 ? null : Integer.toString(spanId);
    }

    private static void bindMdc(ServerWebExchange exchange, String fallbackTid, String realIp) {
        try {
            // 每次刷新都优先 live SkyWalking TID，避免早期雪花号污染后续日志
            String tid = resolveSkyWalkingTraceId(exchange);
            if (tid == null) {
                tid = fallbackTid;
            }
            SkyWalkingTraceMdc.putTraceAndSpan(tid, resolveSkyWalkingSpanId(exchange));
            SkyWalkingTraceMdc.putClientIp(realIp);
        } catch (Exception e) {
            log.warn("设置 MDC 失败", e);
        }
    }

    private static void writeTraceHeader(ServerWebExchange exchange, String fallbackTid) {
        try {
            String tid = resolveSkyWalkingTraceId(exchange);
            if (tid == null) {
                tid = fallbackTid;
            }
            if (tid != null && !exchange.getResponse().isCommitted()) {
                exchange.getResponse().getHeaders().set(TRACE_ID_HEADER, tid);
            }
        } catch (Exception e) {
            log.warn("回写 trace 响应头失败", e);
        }
    }

    @Override
    public int getOrder() {
        // 与 SkyWalking Gateway 插件示例一致，保证进入 filter 时 TraceContext 可用
        return -100;
    }
}
