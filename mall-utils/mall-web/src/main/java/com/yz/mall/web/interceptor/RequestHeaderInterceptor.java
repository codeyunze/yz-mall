package com.yz.mall.web.interceptor;

import com.yz.mall.base.HeaderConstants;
import com.yz.mall.base.trace.SkyWalkingTraceMdc;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 请求头拦截器：写入 SkyWalking trace/span 与客户端 IP 到 MDC，供日志与 Logstash 采集。
 *
 * @author yunze
 * @since 2025/11/7 12:22
 */
@Component
public class RequestHeaderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 优先 SkyWalking TraceContext；无 Agent 时回退请求头 x-trace-id
        String headerTraceId = request.getHeader(HeaderConstants.TRACE_ID_HEADER);
        String traceId = SkyWalkingTraceMdc.putTrace(headerTraceId);
        if (traceId != null) {
            response.setHeader(HeaderConstants.TRACE_ID_HEADER, traceId);
        }

        String ip = request.getHeader(HeaderConstants.USER_IP_HEADER);
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        SkyWalkingTraceMdc.putClientIp(ip);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SkyWalkingTraceMdc.clear();
    }
}
