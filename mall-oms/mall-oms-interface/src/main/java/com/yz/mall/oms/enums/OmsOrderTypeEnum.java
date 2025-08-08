package com.yz.mall.oms.enums;

import lombok.Getter;

/**
 * 订单类型枚举: 0正常订单；1秒杀订单
 *
 * @author yunze
 * @date 2024/11/23 星期六 14:31
 */
@Getter
public enum OmsOrderTypeEnum {

    /**
     * 0正常订单
     */
    NORMAL(0, "正常订单"),
    /**
     * 1秒杀订单
     */
    SECKILL(1, "秒杀订单");

    private final Integer type;

    private final String tips;


    OmsOrderTypeEnum(Integer type, String tips) {
        this.type = type;
        this.tips = tips;
    }
}
