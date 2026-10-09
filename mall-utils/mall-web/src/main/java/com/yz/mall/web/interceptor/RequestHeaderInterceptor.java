package com.yz.mall.web.interceptor;

import com.yz.mall.base.HeaderConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 请求头拦截器：写入客户端 IP 到 MDC，并回写 SkyWalking traceId 到响应头。
 * <p>
 * 日志中的 TID / Span 由 Agent + {@code TraceIdMDCPatternLogbackLayout} / Logstash Provider 注入，无需再手写 MDC。
 *
 * @author yunze
 * @since 2025/11/7 12:22
 */
@Component
public class RequestHeaderInterceptor implements HandlerInterceptor {

    private static final String CLIENT_IP = "client_ip";
    private static final String SW_NA = "N/A";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tid = TraceContext.traceId();
        if (tid == null || tid.isEmpty() || SW_NA.equals(tid)) {
            tid = request.getHeader(HeaderConstants.TRACE_ID_HEADER);
        }
        if (tid != null && !tid.isEmpty()) {
            response.setHeader(HeaderConstants.TRACE_ID_HEADER, tid);
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
    }
}
