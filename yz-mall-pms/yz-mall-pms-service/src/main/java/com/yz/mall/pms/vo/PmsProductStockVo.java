package com.yz.mall.pms.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品库存信息
 *
 * @author yunze
 * @since 2024-12-20 13:08:05
 */
@Data
public class PmsProductStockVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品Id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品标签
     */
    private String titles;

    /**
     * 商品图片id，限制为5张，以逗号分割
     */
    private String albumPics;

    /**
     * 商品库存数量
     */
    private Integer quantity;
}

