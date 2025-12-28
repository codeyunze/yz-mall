package com.yz.mall.base.exception;

/**
 * 其他异常
 * @author yunze
 * @date 2024/6/19 星期三 22:55
 */
public class OtherException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;

    public OtherException() {}

    public OtherException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
