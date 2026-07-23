package com.yz.mall.pms.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 跨服务查询用的 SKU 精简信息
 */
@Data
public class ExtendPmsSkuSlimVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SKU Id
     */
    private Long id;

    /**
     * 商品 Id
     */
    private Long productId;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * 售价（分）
     */
    private Long priceFee;

    /**
     * 市场价（分）
     */
    private Long marketPriceFee;

    /**
     * SKU图片文件Id，逗号分隔
     */
    private String albumPics;

    /**
     * 销售属性JSON
     */
    private String attrsJson;

    /**
     * 属性组合键
     */
    private String attrsKey;
}
