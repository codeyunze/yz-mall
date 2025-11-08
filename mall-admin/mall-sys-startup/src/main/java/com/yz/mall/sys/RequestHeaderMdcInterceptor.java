package com.yz.mall.sys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author yunze
 * @since 2025/11/7 12:22
 */
@Component
public class RequestHeaderMdcInterceptor implements HandlerInterceptor {

    private static final String TRACE_ID_HEADER = "X-Trace-ID";
    private static final String USER_IP_HEADER = "X-Forwarded-For";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取 traceId
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId != null && !traceId.isEmpty()) {
            MDC.put("trace_id", traceId);
        }

        // 获取客户端IP
        String ip = request.getHeader(USER_IP_HEADER);
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        MDC.put("client_ip", ip);

        // 可以继续添加其他 header
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理 MDC，防止内存泄漏和线程复用污染
        MDC.clear();
    }

}
