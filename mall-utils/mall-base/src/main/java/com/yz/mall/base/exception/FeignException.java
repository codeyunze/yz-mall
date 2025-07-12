package com.yz.mall.base.exception;

/**
 * openFeign调用异常
 * @author yunze
 * @date 2024/6/19 星期三 22:55
 */
public class FeignException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;

    public FeignException() {}

    public FeignException(Integer code, String message) {
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
