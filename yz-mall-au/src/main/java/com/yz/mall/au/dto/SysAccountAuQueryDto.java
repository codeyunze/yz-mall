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
     * 创建时间
     */
    private LocalDateTime createTime;

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


}

