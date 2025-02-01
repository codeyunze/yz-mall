package com.yz.mall.oms.enums;

import lombok.Getter;

/**
 * 订单状态枚举: 0待付款；1待发货；2已发货；3待收货；4已完成；5已关闭；6无效订单
 *
 * @author yunze
 * @date 2024/11/23 星期六 14:31
 */
@Getter
public enum OmsOrderStatusEnum {

    /**
     * 0待付款
     */
    PENDING_PAYMENT(0, "待付款"),
    /**
     * 1待发货
     */
    PENDING_SHIPMENT(1, "待发货"),
    /**
     * 2已发货
     */
    ALREADY_SHIPPED(2, "已发货"),
    /**
     * 3待收货
     */
    PENDING_RECEIPT(3, "待收货"),
    /**
     * 4已完成
     */
    ORDER_COMPLETED(4, "已完成"),
    /**
     * 5已关闭
     */
    ORDER_CLOSED(5, "已关闭"),
    /**
     * 6无效订单
     */
    ORDER_INVALID(6, "无效订单");

    private final Integer status;

    private final String tips;


    OmsOrderStatusEnum(Integer status, String tips) {
        this.status = status;
        this.tips = tips;
    }
}
