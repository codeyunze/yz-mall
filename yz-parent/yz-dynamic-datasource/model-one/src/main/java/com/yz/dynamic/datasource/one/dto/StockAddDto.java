package com.yz.dynamic.datasource.one.dto;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * 库存信息(TStock)表实体类
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
@Data
public class StockAddDto extends Model<StockAddDto> {

    private static final long serialVesionUID = 1L;

    /**
     * 主键ID
     * @ignore
     */
    private Long id;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 库存数量
     */
    private Integer num;

}

