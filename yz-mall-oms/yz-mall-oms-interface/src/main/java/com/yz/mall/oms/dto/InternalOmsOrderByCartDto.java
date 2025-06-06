package com.yz.mall.oms.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 根据购物车选择信息生成订单参数信息
 *
 * @author yunze
 * @date 2025/1/30 19:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InternalOmsOrderByCartDto extends BaseOmsOrder implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 订单商品信息(在购物车里选择的商品)
     */
    @NotEmpty(message = "商品信息不能为空")
    private List<InternalOmsOrderProductDto> products;
}
