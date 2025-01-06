package com.yz.mall.au.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 个人黄金账户(SysAccountAu)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-01-05 10:06:32
 */
@Data
public class SysAccountAuQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 交易开始时间
     */
    private LocalDateTime startTimeFilter;

    /**
     * 交易结束时间
     */
    private LocalDateTime endTimeFilter;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 交易类型：0买入，1卖出
     */
    private Integer transactionType;

    /**
     * 关联交易Id
     */
    private Long relationId;

    /**
     * 盈利情况：0盈利，1亏损
     */
    private Integer profit;
}

