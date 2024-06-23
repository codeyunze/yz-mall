package com.yz.mall.oms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 订单商品关联表(OmsOrderProductRelation)表实体类
 *
 * @author yunze
 * @since 2024-06-18 12:51:38
 */
@Data
public class OmsOrderProductRelation extends Model<OmsOrderProductRelation> {

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
     * 订单id
     */
    private String orderId;

    /**
     * 商品id
     */
    private String productId;

    /**
     * 购买商品数量
     */
    private Integer productQuantity;

    /**
     * 商品优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 商品优惠后的实际价格
     */
    private BigDecimal realAmount;

    /**
     * 商品属性;[{key:颜色,value:黑色},{key:内存,value:32G}]
     */
    private String productAttributes;

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

