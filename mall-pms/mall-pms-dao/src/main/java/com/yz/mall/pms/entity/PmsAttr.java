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
 * 商品规格属性表(PmsAttr)表实体类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_attr")
public class PmsAttr extends Model<PmsAttr> {

    /**
     * 主键标识
     */
    private Long id;

    /**
     * product_id或sku_id
     */
    private Long relatedId;

    /**
     * 属性类型：0商品；1SKU
     */
    private Integer attrType;

    /**
     * 必选属性：0非必选；1必选
     */
    private Integer attrRequired;

    /**
     * 规格属性名称
     */
    private String attrName;

    /**
     * 规格属性值
     */
    private String attrValue;

    /**
     * 规格属性描述
     */
    private String attrDesc;

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
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
