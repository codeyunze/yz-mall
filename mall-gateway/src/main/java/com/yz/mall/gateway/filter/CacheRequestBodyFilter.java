package com.yz.mall.gateway.filter;

import com.yz.mall.gateway.config.GatewayRequestBodyCacheProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 缓存请求体并回写，解决 WebFlux 请求体只能消费一次的问题。
 * <p>
 * 缓存结果放入 {@link CachedRequestBody}，供访问日志等后续 Filter 读取；
 * 同时用 {@link ServerHttpRequestDecorator} 把同样的字节交给下游。
 * 大文件 / multipart 不缓存，避免把整包文件加载进内存。
 *
 * @author yunze
 * @date 2026/9/21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheRequestBodyFilter implements GlobalFilter, Ordered {

    private final GatewayRequestBodyCacheProperties properties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.isEnabled()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        if (!shouldCache(request)) {
            exchange.getAttributes().put(CachedRequestBody.ATTRIBUTE, CachedRequestBody.skipped(skipReason(request)));
            return chain.filter(exchange);
        }
        // join 无 Body 时为空；chain.filter 返回 Mono<Void> 也会空完成，
        // 不能对 filter 结果 switchIfEmpty，否则会把整条链路再跑一遍。
        return DataBufferUtils.join(request.getBody(), properties.getMaxBodyBytes())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .defaultIfEmpty(new byte[0])
                .flatMap(bytes -> chain.filter(decorate(exchange, bytes)))
                .onErrorResume(DataBufferLimitException.class, e -> handleBodyTooLarge(exchange, request));
    }

    @Override
    public int getOrder() {
        // TraceGatewayFilter=-100，本 Filter 缓存后再交给 RequestAccessLogFilter=-90 打印
        return -95;
    }

    /**
     * 写入缓存属性，并在有内容时包装可重复读的请求。
     *
     * @param exchange 当前交换器
     * @param bodyBytes 已读出的请求体
     */
    private ServerWebExchange decorate(ServerWebExchange exchange, byte[] bodyBytes) {
        ServerWebExchange target = exchange;
        if (bodyBytes.length > 0) {
            ServerHttpRequest decorated = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return Flux.just(bodyBytes).map(bytes -> exchange.getResponse().bufferFactory().wrap(bytes));
                }
            };
            target = exchange.mutate().request(decorated).build();
        }
        target.getAttributes().put(CachedRequestBody.ATTRIBUTE, CachedRequestBody.of(bodyBytes));
        return target;
    }

    /**
     * 请求体超过缓存上限时无法再转发给下游，返回 413。
     */
    private Mono<Void> handleBodyTooLarge(ServerWebExchange exchange, ServerHttpRequest request) {
        log.warn("网关请求体超过 maxBodyBytes，拒绝转发 method={} path={} maxBodyBytes={}",
                request.getMethod(), request.getURI().getPath(), properties.getMaxBodyBytes());
        exchange.getResponse().setStatusCode(HttpStatus.PAYLOAD_TOO_LARGE);
        return exchange.getResponse().setComplete();
    }

    /**
     * 大文件、multipart 或超长 Content-Length 不缓存 Body。
     */
    private boolean shouldCache(ServerHttpRequest request) {
        MediaType contentType = request.getHeaders().getContentType();
        if (contentType != null && isBinary(contentType)) {
            return false;
        }
        long contentLength = request.getHeaders().getContentLength();
        return contentLength < 0 || contentLength <= properties.getMaxBodyBytes();
    }

    private boolean isBinary(MediaType contentType) {
        String type = contentType.getType();
        return "image".equalsIgnoreCase(type)
                || "audio".equalsIgnoreCase(type)
                || "video".equalsIgnoreCase(type)
                || MediaType.MULTIPART_FORM_DATA.includes(contentType)
                || MediaType.APPLICATION_OCTET_STREAM.includes(contentType);
    }

    private String skipReason(ServerHttpRequest request) {
        MediaType contentType = request.getHeaders().getContentType();
        if (contentType != null && isBinary(contentType)) {
            return "[skipped, contentType=" + contentType + "]";
        }
        long contentLength = request.getHeaders().getContentLength();
        if (contentLength > properties.getMaxBodyBytes()) {
            return "[skipped, contentLength=" + contentLength + " exceeds maxBodyBytes]";
        }
        return "[skipped]";
    }
}
