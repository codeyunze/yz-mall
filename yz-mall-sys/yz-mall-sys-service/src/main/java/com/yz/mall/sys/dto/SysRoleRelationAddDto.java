package com.yz.mall.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统-关联角色数据表(SysRoleRelation)表新增数据模型类
 *
 * @author yunze
 * @since 2024-11-17 19:56:00
 */
@Data
public class SysRoleRelationAddDto implements Serializable {

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

