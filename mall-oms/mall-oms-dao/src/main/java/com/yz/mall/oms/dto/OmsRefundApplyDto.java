package com.yz.mall.oms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户申请退款入参
 *
 * @author yunze
 * @since 2026-07-19
 */
@Data
public class OmsRefundApplyDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    @NotNull(message = "订单不能为空")
    private Long orderId;

    /**
     * 原因类型（可选）
     */
    private Integer reasonType;

    /**
     * 退款说明
     */
    @NotBlank(message = "退款原因不能为空")
    private String reason;
}
