package com.yz.mall.pms.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 产品管理-商品出库日志信息
 *
 * @author yunze
 * @since 2024-12-27 12:50:27
 */
@Data
public class PmsStockOutDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 创建人
     */
    private Long createId;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 关联商品Id
     */
    private Long productId;

    /**
     * 出库编号
     */
    private String stockOutCode;

    /**
     * 本次出库数量
     */
    private Integer quantity;

    /**
     * 关联订单Id
     */
    private Long orderId;

    /**
     * 商品入库备注信息
     */
    private String remark;


    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品标签
     */
    private String titles;

    /**
     * 商品图片id，限制为5张，以逗号分割
     */
    private String albumPics;

    /**
     * 关联SKU Id
     */
    private Long skuId;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * 商品分类ID
     */
    private Long categoryId;

    /**
     * 商品分类名称
     */
    private String categoryName;
}

