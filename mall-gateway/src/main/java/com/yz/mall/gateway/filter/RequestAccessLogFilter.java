package com.yz.mall.gateway.filter;

import com.yz.mall.gateway.config.GatewayAccessLogProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 网关全局访问日志：打印每个请求的 URI、查询参数、Header、Cookie、Body，以及整段链路耗时。
 * <p>
 * Body 来自前置 {@link CacheRequestBodyFilter} 写入的 {@link CachedRequestBody}，本 Filter 不消费请求流。
 *
 * @author yunze
 * @date 2026/9/21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestAccessLogFilter implements GlobalFilter, Ordered {

    private static final String MASK = "***";
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "authorization", "cookie", "set-cookie", "token", "access_token",
            "refresh_token", "password", "secret", "satoken", "x-sa-token");
    private static final Pattern JSON_SECRET = Pattern.compile("(?i)(\"(?:password|token|secret|authorization)\"\\s*:\\s*\")[^\"]*");
    private static final Pattern FORM_SECRET = Pattern.compile("(?i)((?:password|token|secret)=)[^&]*");

    private final GatewayAccessLogProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.isEnabled() || !log.isInfoEnabled()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (shouldSkip(path)) {
            return chain.filter(exchange);
        }
        long startNanos = System.nanoTime();
        String body = resolveBody(exchange, request);
        return chain.filter(exchange).doFinally(signalType -> {
            long costMs = (System.nanoTime() - startNanos) / 1_000_000L;
            HttpStatusCode status = exchange.getResponse().getStatusCode();
            log.info("网关请求 method={} uri={} path={} query={} headers={} cookies={} remote={} contentType={} body={} status={} cost={}ms",
                    request.getMethod(),
                    request.getURI(),
                    path,
                    formatParams(request.getQueryParams()),
                    formatHeaders(request.getHeaders()),
                    formatCookies(request.getCookies()),
                    formatRemote(request),
                    request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE),
                    body,
                    status == null ? "-" : status.value(),
                    costMs);
        });
    }

    @Override
    public int getOrder() {
        // 紧随 CacheRequestBodyFilter（-95），日志里能读到已缓存 Body 以及 x-real-ip / x-trace-id
        return -90;
    }

    /**
     * 从前置缓存读取 Body 文本；未缓存时打印跳过原因或空串。
     *
     * @param exchange 当前交换器
     * @param request 原请求（取 charset）
     */
    private String resolveBody(ServerWebExchange exchange, ServerHttpRequest request) {
        CachedRequestBody cached = CachedRequestBody.from(exchange);
        if (cached == null) {
            return "";
        }
        if (cached.isSkipped()) {
            return cached.getSkipReason();
        }
        return formatBody(request, cached.getBytes());
    }

    /**
     * 将请求体转为单行文本，按需脱敏并截断。
     *
     * @param request 原请求（取 charset）
     * @param bytes 请求体字节
     */
    private String formatBody(ServerHttpRequest request, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        String body = new String(bytes, resolveCharset(request));
        if (properties.isMaskSensitive()) {
            body = JSON_SECRET.matcher(body).replaceAll("$1" + MASK);
            body = FORM_SECRET.matcher(body).replaceAll("$1" + MASK);
        }
        body = body.replaceAll("\\s+", " ").trim();
        int max = properties.getMaxBodyLogLength();
        if (max > 0 && body.length() > max) {
            return body.substring(0, max) + "...(truncated, bytes=" + bytes.length + ")";
        }
        return body;
    }

    private Charset resolveCharset(ServerHttpRequest request) {
        MediaType contentType = request.getHeaders().getContentType();
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        }
        return StandardCharsets.UTF_8;
    }

    /**
     * 判断路径是否命中跳过规则。
     *
     * @param path 请求路径
     * @return true 表示不打印
     */
    private boolean shouldSkip(String path) {
        List<String> skipPaths = properties.getSkipPaths();
        if (skipPaths == null || skipPaths.isEmpty() || path == null) {
            return false;
        }
        for (String pattern : skipPaths) {
            if (pattern != null && pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化查询参数，按需脱敏。
     *
     * @param params 查询参数
     */
    private String formatParams(MultiValueMap<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "{}";
        }
        Map<String, List<String>> result = new LinkedHashMap<>();
        params.forEach((name, values) -> result.put(name, maskValues(name, values)));
        return result.toString();
    }

    /**
     * 格式化请求头；Cookie 头改由 cookies 字段单独打印，避免重复。
     *
     * @param headers 请求头
     */
    private String formatHeaders(HttpHeaders headers) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        headers.forEach((name, values) -> {
            if (HttpHeaders.COOKIE.equalsIgnoreCase(name)) {
                return;
            }
            result.put(name, maskValues(name, values));
        });
        return result.toString();
    }

    /**
     * 格式化 Cookie 名与值。
     *
     * @param cookies 请求 Cookie
     */
    private String formatCookies(MultiValueMap<String, HttpCookie> cookies) {
        if (cookies == null || cookies.isEmpty()) {
            return "{}";
        }
        Map<String, List<String>> result = new LinkedHashMap<>();
        cookies.forEach((name, values) -> {
            List<String> cookieValues = new ArrayList<>(values.size());
            for (HttpCookie cookie : values) {
                cookieValues.add(maskValue(name, cookie.getValue()));
            }
            result.put(name, cookieValues);
        });
        return result.toString();
    }

    private String formatRemote(ServerHttpRequest request) {
        String realIp = request.getHeaders().getFirst("x-real-ip");
        if (realIp != null && !realIp.isEmpty()) {
            return realIp;
        }
        InetSocketAddress remote = request.getRemoteAddress();
        return remote == null ? "-" : remote.toString();
    }

    private List<String> maskValues(String name, List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        List<String> masked = new ArrayList<>(values.size());
        for (String value : values) {
            masked.add(maskValue(name, value));
        }
        return masked;
    }

    private String maskValue(String name, String value) {
        if (!properties.isMaskSensitive() || value == null || !isSensitive(name)) {
            return value;
        }
        return MASK;
    }

    private boolean isSensitive(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        String key = name.toLowerCase(Locale.ROOT);
        if (SENSITIVE_KEYS.contains(key)) {
            return true;
        }
        return key.contains("token") || key.contains("secret") || key.contains("password");
    }
}
