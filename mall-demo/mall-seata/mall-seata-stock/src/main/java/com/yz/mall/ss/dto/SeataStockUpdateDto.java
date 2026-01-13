package com.yz.mall.ss.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 分布式事务-库存表(SeataStock)表更新数据模型类
 *
 * @author yunze
 * @since 2025-11-26 15:29:48
 */
@Data
public class SeataStockUpdateDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 商品Id
     */
    private Long productId;

    /**
     * 库存数量
     */
    private Integer productStock;


}

