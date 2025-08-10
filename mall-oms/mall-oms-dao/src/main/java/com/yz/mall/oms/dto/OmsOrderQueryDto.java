package com.yz.mall.oms.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单信息表(OmsOrder)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-01-30 19:13:00
 */
@Data
public class OmsOrderQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户Id
     * @ignore
     */
    private Long userId;

    /**
     * 订单编号;省市区年月日000001
     */
    private String orderCode;

    /**
     * 订单状态：0待付款；1待发货；2已发货；3待收货；4已完成；5已关闭/已取消；6无效订单
     */
    private Integer orderStatus;

    /**
     * 排除订单状态：0待付款；1待发货；2已发货；3待收货；4已完成；5已关闭/已取消；6无效订单
     * @ignore
     */
    private List<Integer> excludeOrderStatuses;

    /**
     * 订单类型：0正常订单；1秒杀订单
     */
    private Integer orderType;

    /**
     * 收货状态：0未确认收货；1已确认收货
     */
    private Integer confirmStatus;

    /**
     * 支付方式：0未支付/待支付；1支付宝；2微信
     */
    private Integer payType;

    /**
     * 订单实际应付金额
     */
    private BigDecimal payAmount;

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
     * 订单消息接收邮箱
     */
    private String email;
}

