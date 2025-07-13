package com.yz.mall.base.exception;

/**
 * 业务数据已经存在异常
 * @author yunze
 * @date 2024/11/23 星期六 14:45
 */
public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException() {
        super("数据已经存在");
    }
}
