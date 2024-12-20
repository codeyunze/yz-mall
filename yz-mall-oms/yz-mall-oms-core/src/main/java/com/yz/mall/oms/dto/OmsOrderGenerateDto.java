package com.yz.mall.oms.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单信息表(OmsOrder)表新增数据模型类
 *
 * @author yunze
 * @since 2024-06-18 12:49:55
 */
@Data
public class OmsOrderGenerateDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 用户Id
     */
    @NotBlank(message = "用户Id不能为空")
    private Long userId;

    /**
     * 订单类型：0正常订单；1秒杀订单
     */
    private Integer orderType;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 订单实际应付金额
     */
    private BigDecimal payAmount;

    /**
     * 订单备注
     */
    private String note;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    /**
     * 收货人手机号
     */
    @NotBlank(message = "收货人手机号不能为空")
    private String receiverPhone;

    /**
     * 收货省
     */
    @NotBlank(message = "收货省不能为空")
    private String receiverProvince;

    /**
     * 收货市
     */
    @NotBlank(message = "收货市不能为空")
    private String receiverCity;

    /**
     * 收货区
     */
    @NotBlank(message = "收货区不能为空")
    private String receiverRegion;

    /**
     * 收货详细地址
     */
    @NotBlank(message = "收货详细地址不能为空")
    private String receiverAddress;

    /**
     * 订单消息接收邮箱
     */
    @NotBlank(message = "订单消息接收邮箱不能为空")
    private String email;

    /**
     * 订单商品信息
     */
    @NotNull(message = "商品信息不能为空")
    private List<OmsOrderItemDto> omsOrderItems;
}

