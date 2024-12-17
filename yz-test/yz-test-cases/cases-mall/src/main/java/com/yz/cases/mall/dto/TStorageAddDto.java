package com.yz.cases.mall.dto;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 库存信息(TStock)表实体类
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
@Data
public class TStorageAddDto extends Model<TStorageAddDto> {

    private static final long serialVesionUID = 1L;

    /**
     * 主键ID
     * @ignore
     */
    private Long id;

    /**
     * 商品id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productId;

    /**
     * 库存数量
     */
    private Integer num;

}

