package com.yz.dynamic.datasource.one.dto;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品信息(TProduct)表实体类
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
@Data
public class ProductAddDto extends Model<ProductAddDto> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     * @ignore
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private BigDecimal price;

}

