package com.yz.mall.oms.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 退款单分页查询条件
 *
 * @author yunze
 * @since 2026-07-19
 */
@Data
public class OmsOrderRefundQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 退款单主键（待办跳转定位）
     */
    private Long id;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 申请人用户id（我的退款列表时注入）
     */
    private Long userId;

    /**
     * 退款状态：0待审核；1已通过；2已拒绝；3已取消
     */
    private Integer refundStatus;
}
