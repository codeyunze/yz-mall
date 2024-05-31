package com.yz.dynamic.datasource.two.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 库存信息(TStock)表实体类
 *
 * @author yunze
 * @since 2023-10-31 00:02:31
 */
@Data
public class TStorage extends Model<TStorage> {

    private static final long serialVesionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    /**
     * 商品id
     */
    private Long productId;

    /**
     * 库存数量
     */
    private Integer num;

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

