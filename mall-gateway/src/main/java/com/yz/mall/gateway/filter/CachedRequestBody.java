package com.yz.mall.gateway.filter;

import org.springframework.web.server.ServerWebExchange;

/**
 * 网关已缓存的请求体，放入 {@link ServerWebExchange} 属性供后续 Filter 使用。
 *
 * @author yunze
 * @date 2026/9/21
 */
public final class CachedRequestBody {

    /**
     * Exchange 属性键
     */
    public static final String ATTRIBUTE = CachedRequestBody.class.getName();

    private final byte[] bytes;
    private final String skipReason;

    private CachedRequestBody(byte[] bytes, String skipReason) {
        this.bytes = bytes;
        this.skipReason = skipReason;
    }

    /**
     * 已成功缓存的请求体（可为空字节）。
     *
     * @param bytes 请求体字节，null 视为空
     */
    public static CachedRequestBody of(byte[] bytes) {
        return new CachedRequestBody(bytes == null ? new byte[0] : bytes, null);
    }

    /**
     * 无请求体。
     */
    public static CachedRequestBody empty() {
        return of(new byte[0]);
    }

    /**
     * 未缓存（大文件、multipart 等），后续 Filter 只打印原因。
     *
     * @param reason 跳过原因
     */
    public static CachedRequestBody skipped(String reason) {
        return new CachedRequestBody(new byte[0], reason);
    }

    /**
     * 从当前请求上下文取出缓存体，未缓存时返回 null。
     *
     * @param exchange 当前交换器
     */
    public static CachedRequestBody from(ServerWebExchange exchange) {
        Object value = exchange.getAttribute(ATTRIBUTE);
        return value instanceof CachedRequestBody cached ? cached : null;
    }

    /**
     * 是否因类型或大小跳过缓存。
     */
    public boolean isSkipped() {
        return skipReason != null;
    }

    /**
     * 缓存的请求体字节；跳过缓存时为空数组。
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * 跳过缓存的原因；已缓存时为 null。
     */
    public String getSkipReason() {
        return skipReason;
    }
}
