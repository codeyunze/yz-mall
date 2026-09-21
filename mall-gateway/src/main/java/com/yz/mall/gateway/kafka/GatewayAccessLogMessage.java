package com.yz.mall.gateway.kafka;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 网关访问日志 Kafka 消息体。
 *
 * @author yunze
 * @date 2026/9/21
 */
@Data
public class GatewayAccessLogMessage {

    /**
     * 请求时间
     */
    private LocalDateTime requestTime;
    /**
     * 链路追踪 ID
     */
    private String traceId;
    /**
     * HTTP 方法
     */
    private String method;
    /**
     * 完整 URI
     */
    private String uri;
    /**
     * 请求路径
     */
    private String path;
    /**
     * 查询参数（已脱敏）
     */
    private Map<String, List<String>> query;
    /**
     * 请求头（已脱敏，不含 Cookie）
     */
    private Map<String, List<String>> headers;
    /**
     * Cookie（已脱敏）
     */
    private Map<String, List<String>> cookies;
    /**
     * 客户端 IP
     */
    private String remote;
    /**
     * Content-Type
     */
    private String contentType;
    /**
     * 请求体文本（已脱敏 / 截断）
     */
    private String body;
    /**
     * 响应头（已脱敏）
     */
    private Map<String, List<String>> responseHeaders;
    /**
     * 响应 Content-Type
     */
    private String responseContentType;
    /**
     * 响应体文本（已脱敏 / 截断）
     */
    private String responseBody;
    /**
     * HTTP 状态码
     */
    private Integer status;
    /**
     * 网关耗时（毫秒）
     */
    private Long costMs;
}
