package com.yz.mall.au.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 选择还有剩余克数的买入记录
 *
 * @author yunze
 * @since 2025-01-05 10:06:32
 */
@Data
public class SysAccountAuChooseQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 最低价格
     */
    private BigDecimal price;
}

