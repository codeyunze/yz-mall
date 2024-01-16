package com.yz.mall.seata.tcc.order.entity;

/**
 * 订单状态
 *
 * @author yunze
 * @date 2024/1/14 23:21
 */
public enum OrderStatus {
    /**
     * 下单未支付
     */
    INIT(0),
    /**
     * 下单已支付
     */
    SUCCESS(1),
    /**
     * 下单失败
     */
    FAIL(-1);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
