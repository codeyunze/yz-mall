package com.yz.mall.pms.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品库存信息
 *
 * @author yunze
 * @since 2024-12-20 13:08:05
 */
@Data
public class PmsStockVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SKU Id
     */
    private Long skuId;

    /**
     * 商品库存数量
     */
    private Integer quantity;
}

