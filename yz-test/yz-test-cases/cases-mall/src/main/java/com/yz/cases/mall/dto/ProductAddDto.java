package com.yz.cases.mall.dto;

import com.yz.tools.BaseDto;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 商品信息(TProduct)表实体类
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
@Data
public class ProductAddDto extends BaseDto {

    private static final long serialVesionUID = 1L;

    /**
     * 主键ID
     *
     * @ignore
     */
    private Long id;

    /**
     * 商品名称
     */
    @Length(max = 50)
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品价格
     */
    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;

    /**
     * 商品标签
     * 默认值为-1
     */
    private String title;

    /**
     * 商品备注
     * 默认值为-1
     */
    private String remark;

}

