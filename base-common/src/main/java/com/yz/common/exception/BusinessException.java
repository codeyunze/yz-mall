package com.yz.common.exception;


import com.yz.common.enums.CodeEnum;

/**
 * @ClassName BusinessException
 * @Description 自定义异常类-业务异常
 * @Author yunze
 * @Date 2022/11/13 23:23
 * @Version 1.0
 */
public class BusinessException extends RuntimeException {

    private final Integer code = CodeEnum.BUSINESS_ERROR.get();

    private final String message;

    public BusinessException(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
