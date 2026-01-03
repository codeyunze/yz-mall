package com.yz.mall.pms.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类表(PmsCategory)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Data
public class PmsCategoryQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父分类ID，0表示顶级分类
     */
    private Long parentId;

    /**
     * 分类名称（模糊查询）
     */
    private String categoryName;
}

