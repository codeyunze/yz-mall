package com.yz.mall.pms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * 商品SKU表(PmsSku)表新增数据模型类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Data
public class PmsSkuAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品信息Id
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * SKU编码(商品编码)，唯一
     */
    @Length(max = 36, message = "SKU编码长度不能超过36")
    @NotBlank(message = "SKU编码不能为空")
    private String skuCode;

    /**
     * SKU名称
     */
    @Length(max = 255, message = "SKU名称长度不能超过255")
    @NotBlank(message = "SKU名称不能为空")
    private String skuName;

    /**
     * 售价(单位分)
     */
    @NotNull(message = "售价不能为空")
    private Long priceFee;

    /**
     * 市场价(单位分)
     */
    @NotNull(message = "市场价不能为空")
    private Long marketPriceFee;

    /**
     * 状态（1:启用, 0:禁用, -1:删除）
     */
    private Integer status;

    /**
     * 商品图片id，限制为5张，以逗号分割
     */
    @Length(max = 164, message = "商品图片长度不能超过164")
    private String albumPics;

}
