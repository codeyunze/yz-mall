package com.yz.mall.sys.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统-用户与组织关联角色表(SysUserRelationRole)表更新数据模型类
 *
 * @author yunze
 * @since 2024-11-26 11:46:13
 */
@Data
public class SysUserRelationRoleUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 关联角色Id
     */
    private Long roleId;

    /**
     * 关联用户Id/组织Id
     */
    private Long relationId;


}

