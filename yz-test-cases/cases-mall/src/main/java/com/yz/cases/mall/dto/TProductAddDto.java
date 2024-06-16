package com.yz.cases.mall.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品信息(TProduct)表新增数据模型类
 *
 * @author yunze
 * @since 2024-06-13 08:38:51
 */
@Data
public class TProductAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品价格
     */
    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;

    /**
     * 商品标签
     */
    @NotBlank(message = "商品标签不能为空")
    private String title;

    /**
     * 商品备注
     */
    @NotBlank(message = "商品备注不能为空")
    private String remark;

}

