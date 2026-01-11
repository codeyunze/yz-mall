package com.yz.mall.pms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * 商品规格属性表(PmsAttr)表新增数据模型类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Data
public class PmsAttrAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * product_id或sku_id
     */
    @NotNull(message = "关联ID不能为空")
    private Long relatedId;

    /**
     * 规格属性名称
     */
    @Length(max = 255, message = "属性名称长度不能超过255")
    @NotBlank(message = "属性名称不能为空")
    private String attrName;

    /**
     * 规格属性值
     */
    @Length(max = 255, message = "属性值长度不能超过255")
    @NotBlank(message = "属性值不能为空")
    private String attrValue;

    /**
     * 规格属性描述
     */
    @Length(max = 255, message = "属性描述长度不能超过255")
    private String attrDesc;

}
