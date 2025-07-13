package com.yz.mall.base.enums;

/**
 * 异常信息编码枚举
 * 30000号段为业务相关异常；
 * 50000号段为权限相关异常；
 * 60000号段为数据库相关异常；
 * 99999为其他异常
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
     * 请求没有携带token
     */
    ERROR_TOKEN_NULL(50000, "请登录系统操作"),
    /**
     * 非法token
     */
    ERROR_TOKEN_ILLEGAL(50001, "无效令牌"),
    /**
     * token已经过期
     */
    ERROR_TOKEN_EXPIRED(50002, "令牌过期"),
    /**
     * 权限异常
     */
    AUTHENTICATION_ERROR(50003, "没有操作权限"),
    /**
     * 系统异常
     */
    SYSTEM_ERROR(3, "系统异常"),
    /**
     * 参数异常
     */
    PARAMS_ERROR(30001, "参数异常"),
    /**
     * 格式化异常
     */
    FORMAT_ERROR(30002, "格式化异常"),
    /**
     * 业务数据不存在异常
     */
    NOT_EXIST_ERROR(30003, "数据不存在"),
    /**
     * 数据已经存在异常
     */
    ALREADY_EXISTS_ERROR(30004, "数据已经存在"),
    /**
     * 重复提交异常
     */
    REPEAT_SUBMIT(30005, "重复提交，请稍后再试"),
    /**
     * 数据库相关异常
     */
    DATA_ERROR(60001, "数据异常"),
    /**
     * 其他异常
     */
    OTHER_ERROR(99999, "其他异常");

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
