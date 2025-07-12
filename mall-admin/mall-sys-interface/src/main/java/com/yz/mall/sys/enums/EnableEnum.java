package com.yz.mall.sys.enums;

/**
 * 状态枚举: 1-启用 0-停用
 *
 * @author yunze
 * @date 2024/11/23 星期六 14:31
 */
public enum EnableEnum {

    /**
     * 1-启用
     */
    ENABLE(1),
    /**
     * 0-停用
     */
    Disable(0);

    private final Integer value;

    public Integer get() {
        return value;
    }

    EnableEnum(Integer value) {
        this.value = value;
    }
}
