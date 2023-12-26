package com.yz.tools.enums;

/**
 * 订单状态
 * @author yunze
 * @date 2023/11/9 0009 10:47
 */
public enum OrderStateEnum {

    /**
     * 下单未支付
     */
    UNPAID(0),
    /**
     * 下单已支付
     */
    PAID(1);

    private int state;

    OrderStateEnum(int state) {
        this.state = state;
    }

    public int get() {
        return this.state;
    }
}
