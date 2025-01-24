package com.yz.mall.pms.enums;

/**
 * 商品上架状态枚举: 0：下架，1：上架
 *
 * @author yunze
 * @date 2025/01/22 星期三 07:31
 */
public enum ProductPublishStatusEnum {

    /**
     * 0-下架
     */
    DELISTING(0),
    /**
     * 1-上架
     */
    PUBLISH(1);

    private final Integer value;

    public Integer get() {
        return value;
    }

    ProductPublishStatusEnum(Integer value) {
        this.value = value;
    }
}
