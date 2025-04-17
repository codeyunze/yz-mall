package com.yz.mall.oms.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单信息表(OmsOrder)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-01-30 19:13:00
 */
@Data
public class OmsOrderQuerySlimDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 订单编号: 省市区年月日000001
     */
    private String orderCode;

    /**
     * 订单Id
     */
    private String orderId;
}

