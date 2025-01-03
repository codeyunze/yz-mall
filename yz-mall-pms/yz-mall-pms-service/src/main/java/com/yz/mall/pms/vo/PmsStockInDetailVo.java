package com.yz.mall.pms.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 产品管理-商品入库明细信息
 *
 * @author yunze
 * @since 2024-12-25 19:53:26
 */
@Data
public class PmsStockInDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    private Long id;

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
     * 入库编号
     */
    private String stockInCode;

    /**
     * 本次入库数量
     */
    private Integer quantity;

    /**
     * 关联供应商Id
     */
    private Long supplierId;

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
}

