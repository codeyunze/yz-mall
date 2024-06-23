package com.yz.mall.pms.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 商品库存表(PmsStock)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
@Data
public class PmsStockQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;


    /**
     * 主键标识
     */
    private String id;

    /**
     * 创建人
     */
    private String createdId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    private String updatedId;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 数据是否有效：0数据有效
     */
    private Integer invalid;

    /**
     * 商品信息Id
     */
    private String productId;

    /**
     * 商品库存数量
     */
    private Integer quantity;

}

