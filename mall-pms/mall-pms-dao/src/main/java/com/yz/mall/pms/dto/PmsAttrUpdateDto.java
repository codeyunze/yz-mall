package com.yz.mall.pms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品规格属性表(PmsAttr)表更新数据模型类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Data
public class PmsAttrUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * product_id或sku_id
     */
    private Long relatedId;

    /**
     * 规格属性名称
     */
    private String attrName;

    /**
     * 规格属性值
     */
    private String attrValue;

    /**
     * 规格属性描述
     */
    private String attrDesc;

}
