package com.yz.mall.sys.vo;

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
public class SysAccountAuChooseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 交易价格(元/克)
     */
    private BigDecimal price;

    /**
     * 交易数量(克)
     */
    private Integer quantity;

    /**
     * 交易时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime transactionTime;

    /**
     * 剩余数量(克)
     */
    private Integer surplusQuantity;

    /**
     * 建议卖出价格(元/克)
     */
    private BigDecimal proposalPrice;

    /**
     * 卖出数量(克)
     *
     * @ignore
     */
    private Integer sellQuantity;
}

