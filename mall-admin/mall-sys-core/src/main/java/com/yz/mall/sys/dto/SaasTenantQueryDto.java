package com.yz.mall.sys.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * SaaS-租户主表(SaasTenant)查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2026-04-20 00:00:00
 */
@Data
public class SaasTenantQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编码（模糊查询）
     */
    private String tenantCode;

    /**
     * 租户名称（模糊查询）
     */
    private String tenantName;

    /**
     * 租户状态：0停用，1启用，2初始化中
     */
    private Integer tenantStatus;

    /**
     * 服务标识
     */
    private String serviceCode;
}
