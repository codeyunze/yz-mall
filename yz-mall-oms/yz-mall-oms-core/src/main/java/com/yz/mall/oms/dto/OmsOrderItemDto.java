package com.yz.mall.oms.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单商品关联表(OmsOrderProductRelation)表新增数据模型类
 *
 * @author yunze
 * @since 2024-06-18 12:51:39
 */
@Data
public class OmsOrderItemDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 商品id
     */
    @NotBlank(message = "商品id不能为空")
    private Long productId;

    /**
     * 购买商品数量
     */
    @NotNull(message = "商品数量不能为空")
    private Integer productQuantity;

    /**
     * 商品优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 商品优惠后的实际价格
     */
    private BigDecimal realAmount;

    /**
     * 商品属性;[{key:颜色,value:黑色},{key:内存,value:32G}]
     */
    private String productAttributes;




}

