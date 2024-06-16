package com.yz.mall.product.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 商品表(MallProduct)表实体类
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Data
public class MallProduct extends Model<MallProduct> {

    /**
     * 主键标识
     */
    private String id;

    /**
     * 创建人
     */
    private String createdId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    private String updatedId;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updatedTime;

    /**
     * 数据是否有效：0数据有效
     */
    @TableLogic
    private Integer invalid;

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
    private String title;

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
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}

