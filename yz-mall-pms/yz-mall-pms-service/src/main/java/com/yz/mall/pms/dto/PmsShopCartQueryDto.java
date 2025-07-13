package com.yz.mall.pms.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 购物车数据表(PmsShopCart)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-01-24 10:08:18
 */
@Data
public class PmsShopCartQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户Id
     *
     * @ignore
     */
    private Long userId;
}

