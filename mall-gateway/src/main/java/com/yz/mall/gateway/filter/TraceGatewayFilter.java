package com.yz.mall.gateway.filter;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;
import co.elastic.apm.api.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 网关全局过滤器：补充客户端 IP，显式下发 Elastic APM 追踪头，并回写 {@code x-trace-id}。
 * <p>
 * Spring Cloud Gateway（WebFlux）下 Agent 对出站 HTTP 的自动注入不稳定，
 * 必须在此用 {@link Span#injectTraceHeaders} 写入 {@code traceparent}/{@code tracestate}，
 * 下游 mall-sys 才能延续同一 {@code trace.id}。仅传 {@code x-trace-id} 不会被 APM Agent 识别为父子链路。
 * <p>
 * 同时手动 {@link Transaction#setName}：Gateway 场景 Agent 常落成 {@code POST unknown route}。
 * <p>
 * 响应头必须在 {@code beforeCommit} 中写入：代理完成后响应往往已提交，
 * {@code doOnSuccess} 里再 {@code setHeader} 会静默失败，客户端就看不到 {@code x-trace-id}。
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
    private static final String SPAN_ID = "span.id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder builder = request.mutate();

        String realIp = request.getHeaders().getFirst(REAL_IP_HEADER);
        if (realIp == null || realIp.isEmpty()) {
            realIp = Objects.requireNonNull(request.getRemoteAddress()).getHostString();
            builder.header(REAL_IP_HEADER, realIp);
        }

        Transaction transaction = ElasticApm.currentTransaction();
        // Spring Cloud Gateway 常无法解析路由模板，Agent 默认命名为 "POST unknown route"
        String transactionName = buildHttpName(request);
        transaction.setName(transactionName);
        // exit span：下游作为子 Span；inject 写入 W3C traceparent，保证跨服务同一 trace.id
        Span exitSpan = transaction.startExitSpan("external", "http", "gateway-downstream");
        exitSpan.setName(transactionName);
        exitSpan.injectTraceHeaders((headerName, headerValue) -> {
            if (headerName != null && headerValue != null && !headerValue.isEmpty()) {
                builder.header(headerName, headerValue);
            }
        });

        String tid = resolveTraceId(transaction, request);
        if (tid != null) {
            builder.header(TRACE_ID_HEADER, tid);
        }

        ServerWebExchange modifiedExchange = exchange.mutate().request(builder.build()).build();
        final String clientIp = realIp;
        final String responseTid = tid;
        final String spanId = exitSpan.getId();
        putClientIp(clientIp);
        putSpanId(spanId);

        // 在响应真正提交前写入，避免代理完成后 isCommitted=true 导致写头失败
        modifiedExchange.getResponse().beforeCommit(() -> {
            ensureTraceIdResponseHeader(modifiedExchange, responseTid);
            return Mono.empty();
        });

        // 不在此 activate Span：WebFlux 线程切换会导致 Scope 错乱；traceparent 已写入请求头即可串联下游
        return chain.filter(modifiedExchange)
                .doOnEach(signal -> {
                    putClientIp(clientIp);
                    putSpanId(spanId);
                })
                .doOnError(exitSpan::captureException)
                .doFinally(signalType -> {
                    MDC.remove(CLIENT_IP);
                    MDC.remove(SPAN_ID);
                    exitSpan.end();
                });
    }

    private static String buildHttpName(ServerHttpRequest request) {
        String method = request.getMethod() == null ? "HTTP" : request.getMethod().name();
        String path = request.getURI().getRawPath();
        if (path == null || path.isEmpty()) {
            path = "/";
        }
        return method + " " + path;
    }

    private static String resolveTraceId(Transaction transaction, ServerHttpRequest request) {
        String tid = transaction.getTraceId();
        if (tid != null && !tid.isEmpty()) {
            return tid;
        }
        tid = request.getHeaders().getFirst(TRACE_ID_HEADER);
        return (tid == null || tid.isEmpty()) ? null : tid;
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

    private static void putSpanId(String spanId) {
        try {
            if (spanId != null && !spanId.isEmpty()) {
                MDC.put(SPAN_ID, spanId);
            }
        } catch (Exception e) {
            log.warn("设置 span.id MDC 失败", e);
        }
    }

    /**
     * 下游若已带回 x-trace-id 则保留；否则用网关侧 TID 补上。
     */
    private static void ensureTraceIdResponseHeader(ServerWebExchange exchange, String tid) {
        try {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            String existing = headers.getFirst(TRACE_ID_HEADER);
            if (existing != null && !existing.isEmpty()) {
                return;
            }
            String resolved = tid;
            if (resolved == null || resolved.isEmpty()) {
                resolved = ElasticApm.currentTransaction().getTraceId();
            }
            if (resolved != null && !resolved.isEmpty()) {
                headers.set(TRACE_ID_HEADER, resolved);
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
