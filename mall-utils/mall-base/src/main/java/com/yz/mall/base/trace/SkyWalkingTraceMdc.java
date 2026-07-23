package com.yz.mall.base.trace;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;

/**
 * 将 SkyWalking {@link TraceContext} 写入 SLF4J MDC，供业务日志与 Logstash 采集。
 * <p>
 * 运行时需挂载 SkyWalking Java Agent；未挂载时 {@link TraceContext#traceId()} 为 {@code N/A}，可回退到自定义 header。
 * 网关 WebFlux 场景请优先用 {@code WebFluxSkyWalkingTraceContext} 解析后再调用 {@link #putTraceAndSpan}。
 */
public final class SkyWalkingTraceMdc {

    /**
     * MDC / Logstash 字段：链路追踪 Id
     */
    public static final String TRACE_ID = "trace_id";
    /**
     * MDC / Logstash 字段：当前 Span Id
     */
    public static final String SPAN_ID = "span_id";
    /**
     * MDC / Logstash 字段：客户端 IP
     */
    public static final String CLIENT_IP = "client_ip";

    private static final String SW_NA = "N/A";

    private SkyWalkingTraceMdc() {
    }

    /**
     * 判断 SkyWalking 返回的 id 是否不可用（空或 N/A）。
     *
     * @param id traceId / segmentId 等
     * @return true 表示不可用
     */
    public static boolean isUnavailable(String id) {
        return id == null || id.isEmpty() || SW_NA.equals(id);
    }

    /**
     * 当前线程 SkyWalking traceId；不可用时返回 null。
     *
     * @return traceId 或 null
     */
    public static String currentTraceIdOrNull() {
        String tid = TraceContext.traceId();
        return isUnavailable(tid) ? null : tid;
    }

    /**
     * 优先取 SkyWalking traceId；不可用时返回 fallback（可为 null）。
     *
     * @param fallback 自定义 header 或网关生成的兜底 Id
     * @return 可用的 traceId，均不可用时返回 null
     */
    public static String resolveTraceId(String fallback) {
        String tid = currentTraceIdOrNull();
        if (tid != null) {
            return tid;
        }
        if (fallback != null && !fallback.isEmpty() && !SW_NA.equals(fallback)) {
            return fallback;
        }
        return null;
    }

    /**
     * 当前 SkyWalking spanId；无上下文时返回 null。
     *
     * @return spanId 字符串，或 null
     */
    public static String resolveSpanId() {
        int spanId = TraceContext.spanId();
        return spanId < 0 ? null : Integer.toString(spanId);
    }

    /**
     * 写入 trace_id / span_id 到 MDC（span 取自当前线程 TraceContext）。
     *
     * @param fallbackTraceId SkyWalking 不可用时的兜底 traceId
     * @return 实际写入的 traceId（可能为 null）
     */
    public static String putTrace(String fallbackTraceId) {
        return putTraceAndSpan(resolveTraceId(fallbackTraceId), resolveSpanId());
    }

    /**
     * 按已解析好的值写入 MDC（网关 WebFlux 场景：先用 WebFlux API 解析再写入）。
     *
     * @param traceId 已解析的 traceId，可为 null
     * @param spanId  已解析的 spanId，可为 null
     * @return 写入的 traceId
     */
    public static String putTraceAndSpan(String traceId, String spanId) {
        if (!isUnavailable(traceId)) {
            MDC.put(TRACE_ID, traceId);
        } else {
            MDC.remove(TRACE_ID);
            traceId = null;
        }
        if (spanId != null && !spanId.isEmpty() && !SW_NA.equals(spanId)) {
            MDC.put(SPAN_ID, spanId);
        } else {
            MDC.remove(SPAN_ID);
        }
        return traceId;
    }

    /**
     * 写入客户端 IP 到 MDC。
     *
     * @param clientIp 客户端 IP
     */
    public static void putClientIp(String clientIp) {
        if (clientIp != null && !clientIp.isEmpty()) {
            MDC.put(CLIENT_IP, clientIp);
        }
    }

    /**
     * 清理本工具写入的 MDC 字段。
     */
    public static void clear() {
        MDC.remove(TRACE_ID);
        MDC.remove(SPAN_ID);
        MDC.remove(CLIENT_IP);
    }
}
