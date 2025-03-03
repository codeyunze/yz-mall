package com.yz.mall.oms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 订单信息表(OmsOrder)表新增数据模型类
 *
 * @author yunze
 * @since 2025-01-30 19:13:00
 */
@Data
public class OmsOrderAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 创建人
     */
    @NotNull(message = "创建人不能为空")
    private Long createdId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    @NotNull(message = "更新人不能为空")
    private Long updatedId;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 用户Id
     */
    @NotNull(message = "用户Id不能为空")
    private Long userId;

    /**
     * 订单编号;省市区年月日000001
     */
    @NotBlank(message = "订单编号;省市区年月日000001不能为空")
    private String orderCode;

    /**
     * 订单状态：0待付款；1待发货；2已发货；3待收货；4已完成；5已关闭/已取消；6无效订单
     */
    private Integer orderStatus;

    /**
     * 订单类型：0正常订单；1秒杀订单
     */
    private Integer orderType;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 收货状态：0未确认收货；1已确认收货
     */
    private Integer confirmStatus;

    /**
     * 确认收货时间
     */
    private LocalDateTime receiveTime;

    /**
     * 支付方式：0未支付/待支付；1支付宝；2微信
     */
    private Integer payType;

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
    @NotBlank(message = "订单备注不能为空")
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
    private String receiverDistrict;

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


}

