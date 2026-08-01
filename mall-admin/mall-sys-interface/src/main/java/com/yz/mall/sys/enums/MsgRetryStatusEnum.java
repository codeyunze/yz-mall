package com.yz.mall.sys.enums;

/**
 * 消息重试状态枚举
 *
 * @author yunze
 * @since 2025-01-20
 */
public enum MsgRetryStatusEnum {
    /**
     * 0-重试中
     */
    RETRYING(0, "重试中"),

    /**
     * 1-待处理（需要人工介入）
     */
    PENDING(1, "待处理"),

    /**
     * 2-已处理
     */
    PROCESSED(2, "已处理"),

    /**
     * 3-忽略
     */
    IGNORED(3, "忽略");

    private final Integer code;
    private final String desc;

    MsgRetryStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer get() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

