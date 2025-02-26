package com.yz.mall.file.enums;

/**
 * 文件是否可以访问
 *
 * @author yunze
 * @since 2025/2/16 16:34
 */
public enum YzPublicAccessEnum {

    /**
     * 不公开
     */
    NO_ACCESS(0),
    /**
     * 公开
     */
    ACCESSIBLE(1);

    /**
     * 文件存储模式
     */
    private final int value;

    YzPublicAccessEnum(Integer value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
