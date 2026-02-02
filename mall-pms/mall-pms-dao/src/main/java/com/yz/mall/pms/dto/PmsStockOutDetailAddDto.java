package com.yz.mall.pms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 产品管理-商品出库日志表(PmsStockOutDetail)表新增数据模型类
 *
 * @author yunze
 * @since 2024-12-27 12:50:27
 */
@Data
public class PmsStockOutDetailAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联商品Id
     */
    private Long productId;

    /**
     * 关联SKU Id
     */
    @NotNull(message = "关联SKU Id不能为空")
    private Long skuId;

    /**
     * 本次出库数量
     */
    private Integer quantity;

    /**
     * 关联订单Id
     */
    private Long orderId;

    /**
     * 商品入库备注信息
     */
    private String remark;

    public PmsStockOutDetailAddDto(Long productId, Long skuId, Integer quantity, String remark) {
        this.productId = productId;
        this.skuId = skuId;
        this.quantity = quantity;
        this.remark = remark;
    }
}

