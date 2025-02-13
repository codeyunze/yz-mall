package com.yz.mall.oms.vo;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yz.mall.oms.entity.OmsOrder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单信息表(OmsOrder)表实体类
 *
 * @author yunze
 * @since 2025-01-30 19:12:58
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OmsOrderVo extends Model<OmsOrderVo> {

    /**
     * 订单主键标识 {@link OmsOrder#getId()}
     */
    private Long id;

    /**
     * 订单编号;省市区年月日000001
     */
    private String orderCode;

    /**
     * 订单状态：0待付款；1待发货；2已发货；3待收货；4已完成；5已关闭/已取消/已取消；6无效订单
     */
    private Integer orderStatus;

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
     * 订单下的商品信息
     */
    private List<OmsOrderProductVo> products;
}

