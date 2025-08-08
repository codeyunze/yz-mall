package com.yz.mall.pms.enums;

/**
 * 商品状态枚举: 0：商品正常，1：商品无货，2：商品下架
 *
 * @author yunze
 * @date 2025/01/22 星期三 07:31
 */
public enum ProductStatusEnum {

    /**
     * 0：商品正常
     */
    NORMAL(0),
    /**
     * 1：商品无货/售罄
     */
    SELL_OUT(1),
    /**
     * 2：商品下架
     */
    DELISTING(2);

    private final Integer value;

    public Integer get() {
        return value;
    }

    ProductStatusEnum(Integer value) {
        this.value = value;
    }
}
