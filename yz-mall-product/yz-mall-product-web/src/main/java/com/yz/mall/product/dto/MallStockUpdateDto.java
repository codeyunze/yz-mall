package com.yz.mall.product.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 商品库存表(MallStock)表更新数据模型类
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
@Data
public class MallStockUpdateDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    @NotBlank(message = "主键标识不能为空")
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

