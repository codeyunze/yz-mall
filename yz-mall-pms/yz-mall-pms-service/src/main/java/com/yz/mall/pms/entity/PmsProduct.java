package com.yz.mall.pms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 商品表(PmsProduct)表实体类
 *
 * @author yunze
 * @since 2024-12-20 13:08:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PmsProduct extends Model<PmsProduct> {

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 创建人
     */
    private Long createId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private Long updateId;

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
    private Integer invalid;

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
     * 商品上架状态：0：下架，1：上架 {@link com.yz.mall.pms.enums.ProductPublishStatusEnum}
     */
    private Integer publishStatus;

    /**
     * 商品审核状态：0：未审核，1：审核通过，9：待审核 {@link com.yz.mall.pms.enums.ProductVerifyStatusEnum}
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

