package com.yz.mall.pms.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品表(PmsProduct)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Data
public class PmsProductQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品标签
     */
    private List<String> titles;

    /**
     * 商品上架状态：0：下架，1：上架
     */
    private Integer publishStatus;

    /**
     * 商品审核状态：0：未审核，1：审核通过
     */
    private Integer verifyStatus;

}

