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
 * 商品/SKU图片表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_product_image")
public class PmsProductImage extends Model<PmsProductImage> {

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 业务类型：0=SPU；1=SKU
     */
    private Integer bizType;

    /**
     * 业务Id（product_id 或 sku_id）
     */
    private Long bizId;

    /**
     * 文件服务文件Id
     */
    private Long fileId;

    /**
     * 排序，数值越小越靠前
     */
    private Integer sort;

    /**
     * 是否主图：0否；1是
     */
    private Integer isMain;

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

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
