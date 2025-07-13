package com.yz.mall.pms.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车指定商品的Id和数量
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
@Data
public class InternalPmsCartDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品信息Id
     */
    private Long productId;

    // TODO: 2025/2/5 yunze 购物车需要添加product_sku_id
    /**
     * 商品SKU的id
     */
    private Long productSkuId;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 商品优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 商品优惠后的实际价格
     */
    private BigDecimal realAmount;
}

