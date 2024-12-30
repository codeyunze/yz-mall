package com.yz.mall.web.exception;

/**
 * 重复提交异常
 * @author yunze
 * @date 2024/12/30 星期三一23:45
 */
public class RepeatSubmitException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RepeatSubmitException() {
        super("不允许重复提交，请稍后再试");
    }

    public RepeatSubmitException(String message) {
        super(message);
    }
}
