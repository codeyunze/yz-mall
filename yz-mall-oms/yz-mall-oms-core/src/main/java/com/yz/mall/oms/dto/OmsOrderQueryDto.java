package com.yz.mall.oms.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单信息表(OmsOrder)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-06-18 12:49:55
 */
@Data
public class OmsOrderQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 用户Id
     */
    private Long userId;

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

}

