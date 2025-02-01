package com.yz.mall.pms.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yunze
 * @date 2025/2/1 18:30
 */
@Data
public class InternalPmsStockDeductVo implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 商品Id
     */
    private Long productId;

    /**
     * 库存扣减是否成功
     * true: 扣减成功，false: 扣减失败
     */
    private boolean deductStatus;
}
