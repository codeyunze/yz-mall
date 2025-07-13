package com.yz.mall.pms.dto;

import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 产品管理-商品入库日志表(PmsStockInDetail)表更新数据模型类
 *
 * @author yunze
 * @since 2024-12-25 19:53:27
 */
@Data
public class PmsStockInDetailUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 关联商品Id
     */
    private Long productId;

    /**
     * 入库编号
     */
    private String stockInCode;

    /**
     * 本次入库数量
     */
    private Integer quantity;

    /**
     * 关联供应商Id
     */
    private Long supplierId;


}

