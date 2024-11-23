package com.yz.tools.enums;

/**
 * 异常信息编码枚举
 *
 * @author yunze
 * @version 1.0
 * @date 2022/11/13 23:23
 */
public enum CodeEnum {
    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 业务异常
     */
    BUSINESS_ERROR(1, "业务操作异常"),
    /**
     * 权限异常
     */
    AUTHENTICATION_ERROR(2, "没有权限操作"),
    /**
     * 非法token
     */
    ERROR_TOKEN_ILLEGAL(50008, "无效令牌"),
    /**
     * token已经过期
     */
    ERROR_TOKEN_EXPIRED(50014, "令牌过期"),
    /**
     * 系统异常
     */
    SYSTEM_ERROR(3, "系统异常"),
    /**
     * 业务数据不存在异常
     */
    NOT_EXIST_ERROR(4, "业务数据不存在异常"),
    /**
     * 参数异常
     */
    PARAMS_ERROR(5, "参数异常"),
    /**
     * 格式化异常
     */
    FORMAT_ERROR(6, "格式化异常"),
    /**
     * 其他异常
     */
    OTHER_ERROR(9, "其他异常");

    /**
     * 异常编码
     */
    private final Integer value;

    /**
     * 异常提示信息
     */
    private final String msg;

    CodeEnum(Integer value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public Integer get() {
        return this.value;
    }

    public String getMsg() {
        return this.msg;
    }
}
