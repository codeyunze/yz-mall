package com.yz.mall.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统-用户与组织关联角色表(SysUserRelationRole)表新增数据模型类
 *
 * @author yunze
 * @since 2024-11-26 11:46:13
 */
@Data
public class SysUserRelationRoleAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 关联角色Id
     */
    @NotNull(message = "关联角色Id不能为空")
    private Long roleId;

    /**
     * 关联用户Id/组织Id
     */
    @NotNull(message = "关联用户Id/组织Id不能为空")
    private Long relationId;


}

