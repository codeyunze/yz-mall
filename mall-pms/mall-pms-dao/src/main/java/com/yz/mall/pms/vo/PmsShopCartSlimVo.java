package com.yz.mall.pms.vo;

import com.yz.mall.pms.entity.PmsShopCart;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车基础数据
 *
 * @author yunze
 * @since 2025-01-24 10:08:17
 */
@Data
public class PmsShopCartSlimVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 购物车信息id {@link PmsShopCart#getId()}
     */
    private Long id;

    /**
     * 商品信息Id
     */
    private Long productId;

    /**
     * 商品sku主键id
     */
    private Long productSkuId;

    /**
     * 数量 {@link PmsShopCart#getQuantity()}
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

