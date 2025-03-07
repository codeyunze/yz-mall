package com.yz.mall.oms.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author yunze
 * @date 2025/1/30 19:25
 */
@Data
public class InternalOmsOrderDto extends BaseOmsOrder implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 订单商品信息
     */
    @NotEmpty(message = "商品信息不能为空")
    private List<InternalOmsOrderProductDto> products;

}
