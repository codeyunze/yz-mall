package com.yz.mall.oms.vo;

import com.yz.mall.oms.entity.OmsOrderRelationProduct;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单商品详情信息
 *
 * @author yunze
 * @since 2025-01-30 19:14:03
 */
@Data
public class OmsOrderProductVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识 {@link OmsOrderRelationProduct#getId()}
     */
    private Long id;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 购买商品数量
     */
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

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格(下单时商品的价格)
     */
    private BigDecimal price;

    /**
     * 商品备注信息
     */
    private String remark;

}

