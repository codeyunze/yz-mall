package com.yz.mall.sys.dto;

import java.math.BigDecimal;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 个人黄金账户(SysAccountAu)表新增数据模型类
 *
 * @author yunze
 * @since 2025-01-05 10:06:32
 */
@Data
public class SysAccountAuAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 交易类型：0买入，1卖出
     */
    private Integer transactionType = 0;

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

    /**
     * 交易时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime transactionTime;

    /**
     * 用户Id
     * @ignore
     */
    private Long userId;
}

