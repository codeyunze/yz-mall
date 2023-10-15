package com.yz.redistools.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : yunze
 * @date : 2023/9/19 12:51
 */
@Data
public class ProductAddDto {

    /**
     * 商品名称
     */
    private String name;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 库存
     */
    private Integer stock;
}
