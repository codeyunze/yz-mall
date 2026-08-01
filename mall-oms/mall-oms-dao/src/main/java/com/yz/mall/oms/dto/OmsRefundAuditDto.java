package com.yz.mall.oms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 退款审核入参
 *
 * @author yunze
 * @since 2026-07-19
 */
@Data
public class OmsRefundAuditDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 退款单id
     */
    @NotNull(message = "退款单不能为空")
    private Long refundId;

    /**
     * 是否通过：true通过；false拒绝
     */
    @NotNull(message = "审核结果不能为空")
    private Boolean pass;

    /**
     * 审核备注（拒绝时建议填写）
     */
    private String auditRemark;
}
