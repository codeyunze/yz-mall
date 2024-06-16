package com.yz.cases.mall.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.yz.tools.BaseDto;
import lombok.Data;

/**
 * 商品信息(TProduct)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-06-13 08:38:51
 */
@Data
public class TProductQueryDto extends BaseDto implements Serializable {

    private static final long serialVesionUID = 1L;


    /**
     * 主键id
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
    private Integer invalid;

    /**
     * 商品标签
     */
    private String title;

    /**
     * 商品备注
     */
    private String remark;

}

