package com.yz.mall.sys.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统-用户关联组织数据表(SysUserRelationOrg)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-11-17 20:26:16
 */
@Data
public class SysUserRelationOrgQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 组织Id
     */
    private Long orgId;


}

