package com.yz.mall.pms.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 购物车数据表(PmsShopCart)表更新数据模型类
 *
 * @author yunze
 * @since 2025-01-24 10:08:18
 */
@Data
public class PmsShopCartUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 商品信息Id
     */
    private Long productId;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 用户Id
     */
    private Long userId;


}

