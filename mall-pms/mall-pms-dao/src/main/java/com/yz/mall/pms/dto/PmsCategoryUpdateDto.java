package com.yz.mall.pms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类表(PmsCategory)表更新数据模型类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Data
public class PmsCategoryUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 父分类ID，0表示顶级分类
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类描述
     */
    private String categoryDesc;

    /**
     * 排序权重，数值越大排序越靠前
     */
    private Integer sortOrder;
}

