package com.yz.mall.pms.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品库存表(PmsStock)表更新数据模型类
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
@Data
public class InternalPmsStockDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 订单Id
     */
    // @NotNull(message = "订单Id不能为空")
    private Long orderId;

    /**
     * 商品信息Id
     */
    @NotNull(message = "商品信息Id不能为空")
    private Long productId;

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

