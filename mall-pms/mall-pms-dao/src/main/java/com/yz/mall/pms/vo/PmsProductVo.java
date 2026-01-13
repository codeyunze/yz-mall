package com.yz.mall.pms.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品详细信息
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Data
public class PmsProductVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    private String id;

    /**
     * 创建人
     */
    private String createId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品价格
     */
    private BigDecimal productPrice;

    /**
     * 商品标签
     */
    private String titles;

    /**
     * 商品备注信息
     */
    private String remark;

    /**
     * 商品上架状态：0：下架，1：上架
     */
    private Integer publishStatus;

    /**
     * 商品审核状态：0：未审核，1：审核通过
     */
    private Integer verifyStatus;

    /**
     * 商品图片id，限制为5张，以逗号分割
     */
    private String albumPics;

    /**
     * 商品剩余库存数量
     */
    private Integer quantity;

    /**
     * 商品分类ID
     */
    private Long categoryId;
}

