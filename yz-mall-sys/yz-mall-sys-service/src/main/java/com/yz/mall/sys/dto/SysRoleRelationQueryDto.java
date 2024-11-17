package com.yz.mall.sys.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 系统-关联角色数据表(SysRoleRelation)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-11-17 19:56:00
 */
@Data
public class SysRoleRelationQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

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

