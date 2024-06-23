package com.yz.mall.pms.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品库存表(PmsStock)表更新数据模型类
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
@Data
public class PmsStockDto implements Serializable {

    private static final long serialVesionUID = 1L;

    public PmsStockDto() {
    }

    public PmsStockDto(String productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    /**
     * 商品信息Id
     */
    @NotBlank(message = "商品信息Id不能为空")
    private String productId;

    /**
     * 商品扣减/增加库存数量
     */
    @NotNull(message = "商品数量不能少于1")
    @Min(value = 1, message = "商品数量不能少于1")
    private Integer quantity;

}

