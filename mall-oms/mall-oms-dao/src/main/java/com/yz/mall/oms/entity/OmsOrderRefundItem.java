package com.yz.mall.oms.entity;

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
 * 订单退款明细
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oms_order_refund_item")
public class OmsOrderRefundItem extends Model<OmsOrderRefundItem> {

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 退款单Id
     */
    private Long refundId;

    /**
     * 订单Id
     */
    private Long orderId;

    /**
     * 订单行Id
     */
    private Long orderItemId;

    /**
     * 商品Id
     */
    private Long productId;

    /**
     * SKU Id
     */
    private Long skuId;

    /**
     * 退款数量
     */
    private Integer quantity;

    /**
     * 本行退款金额（分）
     */
    private Long refundAmount;

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
