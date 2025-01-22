package com.yz.mall.pms.enums;

/**
 * 商品审核状态枚举: 0：未审核，1：审核通过，9：待审核
 *
 * @author yunze
 * @date 2025/01/22 星期三 07:31
 */
public enum ProductVerifyStatusEnum {

    /**
     * 0-未审核
     */
    UNAUDITED(0),
    /**
     * 1-审核通过
     */
    APPROVED_REVIEW(1),
    /**
     * 9-待审核
     */
    PENDING_REVIEW(9);

    private final Integer value;

    public Integer get() {
        return value;
    }

    ProductVerifyStatusEnum(Integer value) {
        this.value = value;
    }
}
