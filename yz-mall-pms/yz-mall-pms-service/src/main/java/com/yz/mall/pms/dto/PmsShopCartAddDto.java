package com.yz.mall.pms.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 购物车数据表(PmsShopCart)表新增数据模型类
 *
 * @author yunze
 * @since 2025-01-24 10:08:18
 */
@Data
public class PmsShopCartAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品信息Id
     */
    @NotNull(message = "商品信息Id不能为空")
    private Long productId;

    /**
     * 数量
     */
    private Integer quantity = 1;

    /**
     * 用户Id
     *
     * @ignore
     */
    private Long userId;


}

