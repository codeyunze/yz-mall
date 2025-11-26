package com.yz.mall.ss.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 分布式事务-库存表(SeataStock)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-11-26 15:29:48
 */
@Data
public class SeataStockQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 商品Id
     */
    private Long productId;

    /**
     * 库存数量
     */
    private Integer productStock;


}

