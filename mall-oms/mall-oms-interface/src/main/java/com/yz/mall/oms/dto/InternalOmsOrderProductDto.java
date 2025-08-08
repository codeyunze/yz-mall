package com.yz.mall.oms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yunze
 * @date 2025/1/30 19:25
 */
@Data
public class InternalOmsOrderProductDto implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 商品Id
     */
    @NotNull(message = "商品不能为空")
    private Long productId;

    /**
     * 商品数量
     */
    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量不能小于1")
    private Integer productQuantity;
}
