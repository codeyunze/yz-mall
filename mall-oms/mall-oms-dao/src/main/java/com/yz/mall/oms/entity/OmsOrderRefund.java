package com.yz.mall.oms.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单退款申请表(oms_order_refund)实体类
 *
 * @author yunze
 * @since 2026-07-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OmsOrderRefund extends Model<OmsOrderRefund> {

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
     * 退款单号
     */
    private String refundNo;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 申请人用户id
     */
    private Long userId;

    /**
     * 商家组织id
     */
    private Long businessOrgId;

    /**
     * 退款金额（分）
     */
    private Long refundAmount;

    /**
     * 原因类型
     */
    private Integer reasonType;

    /**
     * 退款说明
     */
    private String reason;

    /**
     * 退款状态：0待审核；1已通过；2已拒绝；3已取消
     */
    private Integer refundStatus;

    /**
     * 审核人
     */
    private Long auditUserId;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime auditTime;

    /**
     * 审核备注
     */
    private String auditRemark;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
