package com.yz.mall.au.dto;

import java.math.BigDecimal;
import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 个人黄金账户(SysAccountAu)表更新数据模型类
 *
 * @author yunze
 * @since 2025-01-05 10:06:32
 */
@Data
public class SysAccountAuUpdateDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 交易类型：0买入，1卖出
     */
    private Integer transactionType;

    /**
     * 交易价格(元/克)
     */
    private BigDecimal price;

    /**
     * 交易数量(克)
     */
    private Integer quantity;

    /**
     * 盈利金额(元)
     */
    private BigDecimal profitAmount;

    /**
     * 关联交易Id
     */
    private Long relationId;


}

