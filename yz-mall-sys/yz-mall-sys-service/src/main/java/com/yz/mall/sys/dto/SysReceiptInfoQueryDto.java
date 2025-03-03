package com.yz.mall.sys.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统-用户收货信息(SysReceiptInfo)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-03-03 15:40:28
 */
@Data
public class SysReceiptInfoQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 创建人
     */
    private Long createId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人手机号
     */
    private String receiverPhone;
}

