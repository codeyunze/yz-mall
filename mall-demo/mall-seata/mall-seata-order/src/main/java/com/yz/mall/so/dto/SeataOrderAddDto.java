package com.yz.mall.so.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


/**
 * 分布式事务-订单表(SeataOrder)表新增数据模型类
 *
 * @author yunze
 * @since 2025-11-24 22:40:04
 */
@Data
public class SeataOrderAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 用户 Id
     */
    @NotNull(message = "用户 Id 不能为空")
    private Long userId;

    /**
     * 订单编号
     */
    @NotBlank(message = "订单编号不能为空")
    private String orderCode;

    /**
     * 订单状态:0-未支付,1-已支付
     */
    private Integer orderStatus;

    /**
     * 商品 Id
     */
    @NotNull(message = "商品 Id 不能为空")
    private Long productId;

    /**
     * 商品金额，单位：分
     */
    @NotNull(message = "商品金额(单位：分)不能为空")
    private Long productPrice;


}

