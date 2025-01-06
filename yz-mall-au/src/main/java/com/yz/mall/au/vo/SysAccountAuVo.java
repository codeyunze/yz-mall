package com.yz.mall.au.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 个人黄金账户(SysAccountAu)表实体类
 *
 * @author yunze
 * @since 2025-01-05 10:06:31
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SysAccountAuVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
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

    /**
     * 交易时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime transactionTime;
}

