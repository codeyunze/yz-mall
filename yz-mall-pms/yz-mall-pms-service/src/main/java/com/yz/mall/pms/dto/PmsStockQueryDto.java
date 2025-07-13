package com.yz.mall.pms.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品库存表(PmsStock)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
@Data
public class PmsStockQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品信息Id
     */
    private String productId;
}

