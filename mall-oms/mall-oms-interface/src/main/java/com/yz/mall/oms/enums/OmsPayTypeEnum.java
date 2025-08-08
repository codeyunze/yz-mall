package com.yz.mall.oms.enums;

import lombok.Getter;

/**
 * 支付方式枚举: 0未支付/待支付；1支付宝；2微信
 *
 * @author yunze
 * @date 2024/11/23 星期六 14:31
 */
@Getter
public enum OmsPayTypeEnum {

    /**
     * 0未支付/待支付
     */
    PENDING_PAY(0, "待支付"),
    /**
     * 1支付宝
     */
    ALIPAY_PAY(1, "支付宝"),
    /**
     * 2微信
     */
    WECHAT_PAY(1, "微信");

    private final Integer type;

    private final String tips;


    OmsPayTypeEnum(Integer type, String tips) {
        this.type = type;
        this.tips = tips;
    }
}
