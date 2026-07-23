package com.yz.mall.oms.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单信息表(OmsOrder)表实体类
 *
 * @author yunze
 * @since 2025-01-30 19:12:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OmsOrder extends Model<OmsOrder> {

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
    private Long invalid;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 订单编号;省市区年月日000001
     */
    private String orderCode;

    /**
     * 订单状态：0待付款；1待发货；2已发货；3待收货；4已完成；5已关闭/已取消；6无效订单；7退款中；8已退款
     */
    private Integer orderStatus;

    /**
     * 售后状态：0无售后；1售后中；2部分退款；3全额退款
     */
    private Integer refundStatus;

    /**
     * 订单类型：0正常订单；1秒杀订单
     */
    private Integer orderType;

    /**
     * 发货时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime deliveryTime;

    /**
     * 收货状态：0未确认收货；1已确认收货
     */
    private Integer confirmStatus;

    /**
     * 确认收货时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime receiveTime;

    /**
     * 支付方式：0未支付/待支付；1支付宝；2微信
     */
    private Integer payType;

    /**
     * 订单总金额（分）
     */
    private Long totalAmount;

    /**
     * 优惠金额（分）
     */
    private Long discountAmount;

    /**
     * 运费金额（分）
     */
    private Long freightAmount;

    /**
     * 订单实际应付金额（分）
     */
    private Long payAmount;

    /**
     * 支付时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime payTime;

    /**
     * 订单备注
     */
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String note;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人手机号
     */
    private String receiverPhone;

    /**
     * 收货省
     */
    private String receiverProvince;

    /**
     * 收货市
     */
    private String receiverCity;

    /**
     * 收货区
     */
    private String receiverDistrict;

    /**
     * 收货详细地址
     */
    private String receiverAddress;

    /**
     * 订单消息接收邮箱
     */
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String email;

    /**
     * 商家组织Id
     */
    private Long businessOrgId;

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

