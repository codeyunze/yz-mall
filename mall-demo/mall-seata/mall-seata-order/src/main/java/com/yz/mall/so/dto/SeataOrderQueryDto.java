package com.yz.mall.so.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 分布式事务-订单表(SeataOrder)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-11-24 22:40:04
 */
@Data
public class SeataOrderQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 订单状态:0-未支付,1-已支付
     */
    private Integer orderStatus;

    /**
     * 商品Id
     */
    private Long productId;

    /**
     * 商品金额，单位：分
     */
    private Long productPrice;


}

