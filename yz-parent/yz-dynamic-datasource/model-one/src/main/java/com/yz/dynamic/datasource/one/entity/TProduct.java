package com.yz.dynamic.datasource.one.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品信息(TProduct)表实体类
 *
 * @author yunze
 * @since 2023-10-29 18:01:13
 */
@Data
public class TProduct extends Model<TProduct> {

    private static final long serialVesionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 逻辑删除，0：有效数据；1：无效数据
     */
    @TableLogic
    private Integer invalid;


}

