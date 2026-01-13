package com.yz.mall.pms.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品SKU表(PmsSku)表实体类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_sku")
public class PmsSku extends Model<PmsSku> {

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
     * 数据是否有效：0数据有效
     */
    @TableLogic(value = "0", delval = "current_timestamp")
    private Long invalid;

    /**
     * 创建人
     */
    private Long createId;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
