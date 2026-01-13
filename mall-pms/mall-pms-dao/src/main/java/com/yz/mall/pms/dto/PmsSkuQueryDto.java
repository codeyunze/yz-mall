package com.yz.mall.pms.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品SKU表(PmsSku)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Data
public class PmsSkuQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品SPU ID
     */
    private Long productId;

    /**
     * SKU编码
     */
    private String skuCode;

}
