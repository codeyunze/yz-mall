package com.yz.mall.sys.vo;

import lombok.Data;

/**
 * 系统-用户关联组织数据
 *
 * @author yunze
 * @since 2024-11-17 20:26:16
 */
@Data
public class InternalSysUserRelationOrgVo {

    /**
     * 组织Id
     */
    private Long orgId;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织路径
     */
    private String orgPathId;
}

