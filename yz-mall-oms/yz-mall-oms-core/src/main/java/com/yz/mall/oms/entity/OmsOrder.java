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
 * 订单信息表(OmsOrder)表实体类
 *
 * @author yunze
 * @since 2024-06-18 12:49:54
 */
@Data
public class OmsOrder extends Model<OmsOrder> {

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
     * 用户Id
     */
    private String userId;

    /**
     * 订单编号;省市区年月日000001
     */
    private String orderCode;

    /**
     * 订单状态：0待付款；1待发货；2已发货；3待收货；4已完成；5已关闭；6无效订单
     */
    private Integer orderStatus;

    /**
     * 订单类型：0正常订单；1秒杀订单
     */
    private Integer orderType;

    /**
     * 发货时间
     */
    private String deliveryTime;

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
     * 支付方式：0未支付；1支付宝；2微信
     */
    private Integer payType;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 订单实际应付金额
     */
    private BigDecimal payAmount;

    /**
     * 订单备注
     */
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
    private String receiverRegion;

    /**
     * 收货详细地址
     */
    private String receiverAddress;

    /**
     * 订单消息接收邮箱
     */
    private String email;

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

