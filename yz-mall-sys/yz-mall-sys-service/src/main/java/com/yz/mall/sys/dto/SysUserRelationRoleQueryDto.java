package com.yz.mall.sys.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 系统-用户与组织关联角色表(SysUserRelationRole)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-11-26 11:46:13
 */
@Data
public class SysUserRelationRoleQueryDto implements Serializable {

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
     * 关联角色Id
     */
    private Long roleId;

    /**
     * 关联用户Id/组织Id
     */
    private Long relationId;


}

