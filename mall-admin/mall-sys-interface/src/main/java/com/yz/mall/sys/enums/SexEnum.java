package com.yz.mall.sys.enums;

/**
 * 性别枚举: 1-女 0-男
 *
 * @author yunze
 * @date 2024/11/23 星期六 14:31
 */
public enum SexEnum {

    /**
     * 1-性别女
     */
    FEMALE(1),
    /**
     * 0-性别男
     */
    MALE(0);

    private final Integer value;

    public Integer get() {
        return value;
    }

    SexEnum(Integer value) {
        this.value = value;
    }
}
