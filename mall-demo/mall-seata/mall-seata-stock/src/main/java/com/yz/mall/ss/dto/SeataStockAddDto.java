package com.yz.mall.ss.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


/**
 * 分布式事务-库存表(SeataStock)表新增数据模型类
 *
 * @author yunze
 * @since 2025-11-26 15:29:48
 */
@Data
public class SeataStockAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 商品Id
     */
    @NotNull(message = "商品Id不能为空")
    private Long productId;

    /**
     * 库存数量
     */
    private Integer productStock;


}

