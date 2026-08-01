package com.yz.mall.oms.enums;

import lombok.Getter;

/**
 * 退款单状态：0待审核；1已通过；2已拒绝；3已取消
 *
 * @author yunze
 * @since 2026-07-19
 */
@Getter
public enum OmsRefundStatusEnum {

    /**
     * 待审核
     */
    PENDING(0, "待审核"),
    /**
     * 已通过
     */
    APPROVED(1, "已通过"),
    /**
     * 已拒绝
     */
    REJECTED(2, "已拒绝"),
    /**
     * 已取消
     */
    CANCELLED(3, "已取消");

    private final Integer status;

    private final String tips;

    OmsRefundStatusEnum(Integer status, String tips) {
        this.status = status;
        this.tips = tips;
    }
}
