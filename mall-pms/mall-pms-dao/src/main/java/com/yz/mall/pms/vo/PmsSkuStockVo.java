package com.yz.mall.pms.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品下各 SKU 库存明细
 *
 * @author yunze
 * @since 2026-07-19
 */
@Data
public class PmsSkuStockVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SKU id
     */
    private Long skuId;

    /**
     * 商品 id
     */
    private Long productId;

    /**
     * SKU 编码
     */
    private String skuCode;

    /**
     * SKU 名称
     */
    private String skuName;

    /**
     * 库存数量
     */
    private Integer quantity;
}
