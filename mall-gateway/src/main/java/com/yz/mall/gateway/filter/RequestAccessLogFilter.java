package com.yz.mall.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.yz.mall.gateway.config.GatewayAccessLogProperties;
import com.yz.mall.gateway.kafka.GatewayAccessLogKafkaProducer;
import com.yz.mall.gateway.kafka.GatewayAccessLogMessage;
import com.yz.mall.json.JacksonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网关全局访问日志：打印请求 URI、参数、Header、Cookie、Body，以及响应头、响应体和耗时。
 * <p>
 * 请求 Body 来自前置 {@link CacheRequestBodyFilter}；响应 Body 在回写客户端时旁路拷贝，不打断原流。
 * Kafka 投递交给 {@link GatewayAccessLogKafkaProducer}，失败不影响转发。
 *
 * @author yunze
 * @date 2026/9/21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestAccessLogFilter implements GlobalFilter, Ordered {

    private static final String MASK = "***";
    private static final String TRACE_ID_HEADER = "x-trace-id";
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "authorization", "cookie", "set-cookie", "token", "access_token",
            "refresh_token", "password", "secret", "satoken", "x-sa-token");
    private static final Pattern JSON_SECRET = Pattern.compile("(?i)(\"(?:password|token|secret|authorization)\"\\s*:\\s*\")[^\"]*");
    private static final Pattern FORM_SECRET = Pattern.compile("(?i)((?:password|token|secret)=)[^&]*");
    private static final Pattern RESPONSE_CODE = Pattern.compile("\"code\"\\s*:\\s*(-?\\d+)");

    private final GatewayAccessLogProperties properties;
    private final ObjectProvider<GatewayAccessLogKafkaProducer> kafkaProducer;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.isEnabled()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (shouldSkip(path)) {
            return chain.filter(exchange);
        }
        GatewayAccessLogKafkaProducer producer = kafkaProducer.getIfAvailable();
        if (!log.isInfoEnabled() && producer == null) {
            return chain.filter(exchange);
        }
        long startNanos = System.nanoTime();
        String requestBody = resolveBody(exchange, request);
        AtomicReference<byte[]> responseBodyBytes = new AtomicReference<>(new byte[0]);
        AtomicReference<String> responseSkipReason = new AtomicReference<>();
        ServerHttpResponse decorated = new AccessLogResponseDecorator(exchange.getResponse(), this, responseBodyBytes, responseSkipReason, Math.max(properties.getMaxBodyLogLength(), 1));
        ServerWebExchange mutated = exchange.mutate().response(decorated).build();
        return chain.filter(mutated).doFinally(signalType -> {
            long costMs = (System.nanoTime() - startNanos) / 1_000_000L;
            ServerHttpResponse response = mutated.getResponse();
            HttpStatusCode status = response.getStatusCode();
            String responseBody = resolveResponseBody(response, responseBodyBytes.get(), responseSkipReason.get());
            GatewayAccessLogMessage message = buildMessage(request, path, requestBody, response, responseBody, status, costMs);
            if (log.isInfoEnabled()) {
                log.info("网关请求 method={} uri={} path={} query={} headers={} cookies={} remote={} contentType={} body={} status={} responseCode={} responseHeaders={} responseContentType={} responseBody={} cost={}ms",
                        message.getMethod(),
                        message.getUri(),
                        message.getPath(),
                        message.getQuery(),
                        message.getHeaders(),
                        message.getCookies(),
                        message.getRemote(),
                        message.getContentType(),
                        message.getBody(),
                        message.getStatus() == null ? "-" : message.getStatus(),
                        message.getResponseCode() == null ? "-" : message.getResponseCode(),
                        message.getResponseHeaders(),
                        message.getResponseContentType(),
                        message.getResponseBody(),
                        message.getCostMs());
            }
            if (producer != null) {
                producer.send(message);
            }
        });
    }

    @Override
    public int getOrder() {
        // 紧随 CacheRequestBodyFilter（-95），日志里能读到已缓存 Body 以及 x-real-ip / x-trace-id
        return -90;
    }

    /**
     * 组装访问日志消息，控制台与 Kafka 共用同一份数据。
     */
    private GatewayAccessLogMessage buildMessage(ServerHttpRequest request, String path, String requestBody,
            ServerHttpResponse response, String responseBody, HttpStatusCode status, long costMs) {
        GatewayAccessLogMessage message = new GatewayAccessLogMessage();
        message.setRequestTime(LocalDateTime.now());
        message.setTraceId(request.getHeaders().getFirst(TRACE_ID_HEADER));
        message.setMethod(request.getMethod() == null ? null : request.getMethod().name());
        message.setUri(request.getURI().toString());
        message.setPath(path);
        message.setQuery(formatParams(request.getQueryParams()));
        message.setHeaders(formatHeaders(request.getHeaders()));
        message.setCookies(formatCookies(request.getCookies()));
        message.setRemote(formatRemote(request));
        message.setContentType(request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        message.setBody(requestBody);
        message.setResponseHeaders(formatHeaders(response.getHeaders()));
        message.setResponseContentType(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        message.setResponseBody(responseBody);
        message.setResponseCode(parseResponseCode(responseBody));
        message.setStatus(status == null ? null : status.value());
        message.setCostMs(costMs);
        return message;
    }

    /**
     * 包装响应：先 join 再回写，避免匿名内部类热加载导致 NoSuchMethodError。
     */
    private static final class AccessLogResponseDecorator extends ServerHttpResponseDecorator {

        private final RequestAccessLogFilter owner;
        private final AtomicReference<byte[]> captured;
        private final AtomicReference<String> skipReason;
        private final int limit;

        private AccessLogResponseDecorator(ServerHttpResponse delegate, RequestAccessLogFilter owner,
                AtomicReference<byte[]> captured, AtomicReference<String> skipReason, int limit) {
            super(delegate);
            this.owner = owner;
            this.captured = captured;
            this.skipReason = skipReason;
            this.limit = limit;
        }

        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            if (!owner.shouldCaptureResponse(getDelegate())) {
                skipReason.set(owner.responseSkipReason(getDelegate()));
                return super.writeWith(body);
            }
            return DataBufferUtils.join(body)
                    .defaultIfEmpty(bufferFactory().wrap(new byte[0]))
                    .flatMap(joined -> {
                        byte[] all = new byte[joined.readableByteCount()];
                        joined.read(all);
                        DataBufferUtils.release(joined);
                        captured.set(all.length <= limit ? all : Arrays.copyOf(all, limit));
                        return super.writeWith(Mono.just(bufferFactory().wrap(all)));
                    });
        }

        @Override
        public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
            return writeWith(Flux.from(body).flatMapSequential(p -> p));
        }
    }

    private boolean shouldCaptureResponse(ServerHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        if (contentType != null && isBinary(contentType)) {
            return false;
        }
        String encoding = headers.getFirst(HttpHeaders.CONTENT_ENCODING);
        if (encoding != null && !encoding.isEmpty() && !"identity".equalsIgnoreCase(encoding)) {
            return false;
        }
        String disposition = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
        return disposition == null || !disposition.toLowerCase(Locale.ROOT).contains("attachment");
    }

    private boolean isBinary(MediaType contentType) {
        String type = contentType.getType();
        return "image".equalsIgnoreCase(type)
                || "audio".equalsIgnoreCase(type)
                || "video".equalsIgnoreCase(type)
                || MediaType.MULTIPART_FORM_DATA.includes(contentType)
                || MediaType.APPLICATION_OCTET_STREAM.includes(contentType);
    }

    private String responseSkipReason(ServerHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        if (contentType != null && isBinary(contentType)) {
            return "[skipped, contentType=" + contentType + "]";
        }
        String encoding = headers.getFirst(HttpHeaders.CONTENT_ENCODING);
        if (encoding != null && !encoding.isEmpty() && !"identity".equalsIgnoreCase(encoding)) {
            return "[skipped, contentEncoding=" + encoding + "]";
        }
        return "[skipped, attachment]";
    }

    private String resolveResponseBody(ServerHttpResponse response, byte[] bytes, String skipReason) {
        if (skipReason != null) {
            return skipReason;
        }
        return formatBody(bytes, resolveCharset(response.getHeaders().getContentType()));
    }

    /**
     * 从统一响应体 {@code {"code":0,...}} 取出业务码；非 JSON 或解析失败返回 null。
     *
     * @param responseBody 已格式化的响应体文本
     */
    private Integer parseResponseCode(String responseBody) {
        if (responseBody == null || !responseBody.startsWith("{")) {
            return null;
        }
        try {
            JsonNode codeNode = JacksonUtil.getObjectMapper().readTree(responseBody).get("code");
            if (codeNode != null && codeNode.isNumber()) {
                return codeNode.intValue();
            }
        } catch (Exception ignored) {
            Matcher matcher = RESPONSE_CODE.matcher(responseBody);
            if (matcher.find()) {
                return Integer.valueOf(matcher.group(1));
            }
        }
        return null;
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
        return formatBody(cached.getBytes(), resolveCharset(request.getHeaders().getContentType()));
    }

    /**
     * 将请求/响应体转为单行文本，按需脱敏并截断。
     *
     * @param bytes 原始字节
     * @param charset 字符集
     */
    private String formatBody(byte[] bytes, Charset charset) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        String body = new String(bytes, charset);
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

    private Charset resolveCharset(MediaType contentType) {
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
    private Map<String, List<String>> formatParams(MultiValueMap<String, String> params) {
        if (params == null || params.isEmpty()) {
            return Map.of();
        }
        Map<String, List<String>> result = new LinkedHashMap<>();
        params.forEach((name, values) -> result.put(name, maskValues(name, values)));
        return result;
    }

    /**
     * 格式化请求/响应头；Cookie / Set-Cookie 不重复打印明文。
     *
     * @param headers HTTP 头
     */
    private Map<String, List<String>> formatHeaders(HttpHeaders headers) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        headers.forEach((name, values) -> {
            if (HttpHeaders.COOKIE.equalsIgnoreCase(name) || HttpHeaders.SET_COOKIE.equalsIgnoreCase(name)) {
                return;
            }
            result.put(name, maskValues(name, values));
        });
        return result;
    }

    /**
     * 格式化 Cookie 名与值。
     *
     * @param cookies 请求 Cookie
     */
    private Map<String, List<String>> formatCookies(MultiValueMap<String, HttpCookie> cookies) {
        if (cookies == null || cookies.isEmpty()) {
            return Map.of();
        }
        Map<String, List<String>> result = new LinkedHashMap<>();
        cookies.forEach((name, values) -> {
            List<String> cookieValues = new ArrayList<>(values.size());
            for (HttpCookie cookie : values) {
                cookieValues.add(maskValue(name, cookie.getValue()));
            }
            result.put(name, cookieValues);
        });
        return result;
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
