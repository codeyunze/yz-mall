package com.yz.mall.oms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 订单支付参数
 *
 * @author yunze
 * @since 2025/4/17 19:31
 */
@Data
public class OmsPayDto {

    /**
     * 支付方式：1支付宝；2微信
     */
    @NotNull(message = "支付方式不能为空")
    private Integer payType;

    /**
     * 业务类型：1订单
     */
    private Integer businessType;

    /**
     * 业务Id
     */
    @NotNull(message = "业务Id不能为空")
    private Long businessId;

}
