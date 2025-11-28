package com.yz.mall.base;

/**
 * 请求头 key 参数管理
 *
 * @author yunze
 * @since 2025/11/27 17:19
 */
public class HeaderConstants {

    /**
     * 单次请求的全局标识 Id
     */
    public static final String TRACE_ID_HEADER = "x-trace-id";

    /**
     * 用户真实 IP
     */
    public static final String USER_IP_HEADER = "x-forwarded-for";
}
