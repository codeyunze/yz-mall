package com.yz.mall.base.trace;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.Marker;

/**
 * 每次打日志前用 SkyWalking 当前上下文刷新 MDC 的 trace_id / span_id。
 * <p>
 * 拦截器只在请求入口写一次 MDC 时，span_id 会一直停在入口 Span 的 {@code 0}；
 * 本过滤器在日志事件创建时读取 {@link TraceContext#spanId()}，SQL/Feign 等子 Span 才能反映真实值。
 */
public class SkyWalkingMdcTurboFilter extends TurboFilter {

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        String tid = SkyWalkingTraceMdc.currentTraceIdOrNull();
        if (tid != null) {
            // 有 live TraceContext 时覆盖拦截器里可能写入的兜底值，保证与 UI 一致
            org.slf4j.MDC.put(SkyWalkingTraceMdc.TRACE_ID, tid);
        }
        int spanId = TraceContext.spanId();
        if (spanId >= 0) {
            org.slf4j.MDC.put(SkyWalkingTraceMdc.SPAN_ID, Integer.toString(spanId));
        }
        String segmentId = TraceContext.segmentId();
        if (!SkyWalkingTraceMdc.isUnavailable(segmentId)) {
            org.slf4j.MDC.put("segment_id", segmentId);
        }
        return FilterReply.NEUTRAL;
    }
}
