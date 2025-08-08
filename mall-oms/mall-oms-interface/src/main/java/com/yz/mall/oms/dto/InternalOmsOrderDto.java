package com.yz.mall.oms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yunze
 * @date 2025/1/30 19:25
 */
@Data
public class InternalOmsOrderDto extends BaseOmsOrder implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 订单商品信息
     */
    @NotNull(message = "商品信息不能为空")
    private List<InternalOmsOrderProductDto> products;

}
