package com.yz.advice.exception;

/**
 * 业务异常信息
 * @author yunze
 * @date 2024/6/19 星期三 22:55
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessException() {}

    public BusinessException(String message) {
        super(message);
    }
}
