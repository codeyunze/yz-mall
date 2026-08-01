package com.yz.mall.pms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品SKU表(PmsSku)表更新数据模型类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Data
public class PmsSkuUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 商品信息Id
     */
    private Long productId;

    /**
     * SKU编码(商品编码)，唯一
     */
    private String skuCode;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * 售价(单位分)
     */
    private Long priceFee;

    /**
     * 市场价(单位分)
     */
    private Long marketPriceFee;

    /**
     * 状态（1:启用, 0:禁用, -1:删除）
     */
    private Integer status;

    /**
     * 商品图片id，限制为5张，以逗号分割
     */
    private String albumPics;

}
