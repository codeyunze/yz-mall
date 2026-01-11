package com.yz.mall.pms.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 产品管理-商品入库日志表(PmsStockInDetail)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-12-25 19:53:27
 */
@Data
public class PmsStockInDetailQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联商品Id
     */
    private Long skuId;

    /**
     * 入库编号
     */
    private String stockInCode;

    /**
     * 关联供应商Id
     */
    private Long supplierId;

}

