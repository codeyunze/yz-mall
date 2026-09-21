package com.yz.mall.web.interceptor;

import co.elastic.apm.api.ElasticApm;
import com.yz.mall.base.HeaderConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 请求头拦截器：写入客户端 IP 到 MDC，并回写 Elastic APM traceId 到响应头。
 * <p>
 * Agent log correlation 通常会注入 {@code trace.id}/{@code transaction.id}；
 * {@code span.id} 在部分场景不会自动进 MDC，此处兜底写入以便 Logstash/ES 关联。
 *
 * @author yunze
 * @since 2025/11/7 12:22
 */
@Component
public class RequestHeaderInterceptor implements HandlerInterceptor {

    private static final String CLIENT_IP = "client_ip";
    private static final String SPAN_ID = "span.id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tid = ElasticApm.currentTransaction().getTraceId();
        if (tid == null || tid.isEmpty()) {
            tid = MDC.get("trace.id");
        }
        if (tid == null || tid.isEmpty()) {
            tid = request.getHeader(HeaderConstants.TRACE_ID_HEADER);
        }
        if (tid != null && !tid.isEmpty()) {
            response.setHeader(HeaderConstants.TRACE_ID_HEADER, tid);
        }

        String spanId = ElasticApm.currentSpan().getId();
        if (spanId != null && !spanId.isEmpty()) {
            MDC.put(SPAN_ID, spanId);
        }

        String ip = request.getHeader(HeaderConstants.USER_IP_HEADER);
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        MDC.put(CLIENT_IP, ip);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(CLIENT_IP);
        MDC.remove(SPAN_ID);
    }
}
