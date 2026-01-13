package com.yz.mall.pms.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品SKU详细信息
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Data
public class PmsSkuVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
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

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private Long createId;

}
