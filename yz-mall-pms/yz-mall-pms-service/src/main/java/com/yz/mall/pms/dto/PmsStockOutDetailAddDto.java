package com.yz.mall.pms.dto;

import java.time.LocalDateTime;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 产品管理-商品出库日志表(PmsStockOutDetail)表新增数据模型类
 *
 * @author yunze
 * @since 2024-12-27 12:50:27
 */
@Data
public class PmsStockOutDetailAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 关联商品Id
     */
    @NotNull(message = "关联商品Id不能为空")
    private Long productId;

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

    public PmsStockOutDetailAddDto(Long productId, Integer quantity, String remark) {
        this.productId = productId;
        this.quantity = quantity;
        this.remark = remark;
    }
}

