package com.yz.mall.pms.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品规格属性表(PmsAttr)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Data
public class PmsAttrQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * product_id或sku_id
     */
    private Long relatedId;

    /**
     * 属性类型：0商品；1SKU
     */
    private Integer attrType;

    /**
     * 规格属性名称
     */
    private String attrName;

    /**
     * 规格属性值
     */
    private String attrValue;

}
