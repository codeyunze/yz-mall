package com.yz.mall.pms.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品表(PmsProduct)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Data
public class PmsProductSlimQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 商品名称/商品标签
     */
    private String queryInfo;

    /**
     * 上一页最后一条商品Id（下一页的第一条数据之前的一条数据）
     */
    private Long lastProductId;
}

