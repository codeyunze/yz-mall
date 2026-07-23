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
 * 商品库存变更流水
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_stock_log")
public class PmsStockLog extends Model<PmsStockLog> {

    /**
     * 主键标识
     */
    private Long id;

    /**
     * SKU Id
     */
    private Long skuId;

    /**
     * 商品Id（冗余）
     */
    private Long productId;

    /**
     * 仓库Id，0默认仓
     */
    private Long warehouseId;

    /**
     * 变更类型：1入库；2出库扣减；3锁库；4解锁；5回补
     */
    private Integer changeType;

    /**
     * 变更数量（正数）
     */
    private Integer changeQty;

    /**
     * 变更前可售数量
     */
    private Integer beforeQty;

    /**
     * 变更后可售数量
     */
    private Integer afterQty;

    /**
     * 业务单号
     */
    private String bizNo;

    /**
     * 关联订单Id
     */
    private Long orderId;

    /**
     * 备注
     */
    private String remark;

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
