package com.yz.mall.pms.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品展示信息
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Data
public class PmsProductDisplayInfoVo implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    private String id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品标签
     */
    private String titles;

    /**
     * 商品备注信息
     */
    private String remark;

    /**
     * 商品图片id，限制为5张，以逗号分割
     */
    private String albumPics;

    /**
     * 商品销量
     */
    private Integer quantity = 0;
}

