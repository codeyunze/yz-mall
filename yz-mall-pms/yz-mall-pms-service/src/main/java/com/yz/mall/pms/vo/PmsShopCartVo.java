package com.yz.mall.pms.vo;

import com.yz.mall.pms.entity.PmsShopCart;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车数据
 *
 * @author yunze
 * @since 2025-01-24 10:08:17
 */
@Data
public class PmsShopCartVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 商品信息Id
     */
    private Long productId;

    /**
     * 数量 {@link PmsShopCart#getQuantity()}
     */
    private Integer quantity;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品图片id，限制为5张，以逗号分割
     */
    private String albumPics;

    /**
     * 商品备注信息
     */
    private String remark;

    /**
     * 商品状态 {@link com.yz.mall.pms.enums.ProductStatusEnum}
     */
    private Integer productStatus;

    /**
     * 图片预览地址
     */
    private String previewAddress;
}

