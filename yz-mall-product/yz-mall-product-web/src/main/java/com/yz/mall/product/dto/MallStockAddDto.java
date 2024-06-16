package com.yz.mall.product.dto;

import java.time.LocalDateTime;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 商品库存表(MallStock)表新增数据模型类
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
@Data
public class MallStockAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 商品信息Id
     */
    @NotBlank(message = "商品信息Id不能为空")
    private String productId;

    /**
     * 商品库存数量
     */
    private Integer quantity;

}

