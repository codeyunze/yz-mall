package com.yz.mall.pms.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品信息
 *
 * @author yunze
 * @since 2024-12-20 13:08:05
 */
@Data
public class ExtendPmsProductSlimVo implements Serializable {

    private static final long serialVersionUID = 43L;

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品价格（单位：分）
     */
    private Long productPrice;

    /**
     * 商品标签
     */
    private String titles;

    /**
     * 商品图片id，限制为5张，以逗号分割
     */
    private String albumPics;

}

