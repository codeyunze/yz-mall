package com.yz.mall.security;


/**
 * 用户信息上下文信息
 *
 * @author yunze
 * @since 2025/5/18 21:41
 */
public class UserInfoContextHolder {

    private static final ThreadLocal<UserInfo> userContextHolder = new ThreadLocal<>();

    public static void set(UserInfo info) {
        userContextHolder.set(info);
    }

    public static UserInfo get() {
        return userContextHolder.get();
    }

    public static void clear() {
        userContextHolder.remove();
    }

}
