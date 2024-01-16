package com.yz.mall.seata.tcc.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单信息(TOrder)表实体类
 *
 * @author yunze
 * @since 2023-11-05 19:59:16
 */
@Data
public class TccOrderDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 用户ID
     */
    private Long accountId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 订单金额
     */
    private BigDecimal amount;
}

