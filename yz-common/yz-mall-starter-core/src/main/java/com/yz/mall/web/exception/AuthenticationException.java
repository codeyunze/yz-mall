package com.yz.mall.web.exception;

/**
 * 无访问业务权限
 * @author yunze
 * @since 2025/2/26 11:25
 */
public class AuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException() {
        super("无访问业务权限");
    }

    public AuthenticationException(String message) {
        super(message);
    }
}
