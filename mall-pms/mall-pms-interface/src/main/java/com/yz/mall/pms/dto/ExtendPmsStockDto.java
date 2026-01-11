package com.yz.mall.pms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * 商品库存表(PmsStock)表更新数据模型类
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
@Data
public class ExtendPmsStockDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单Id
     */
    // @NotNull(message = "订单Id不能为空")
    private Long orderId;

    /**
     * 商品信息Id
     */
    private Long productId;

    /**
     * SKU信息Id
     */
    @NotNull(message = "SKU信息Id不能为空")
    private Long skuId;

    /**
     * 商品扣减/增加库存数量
     */
    @NotNull(message = "商品数量不能少于1")
    @Min(value = 1, message = "商品数量不能少于1")
    private Integer quantity;


    /**
     * 商品入库备注信息
     */
    @Length(max = 255, message = "商品入库备注说明不能超过255个字符")
    private String remark;
}

