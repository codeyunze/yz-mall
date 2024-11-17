package com.yz.mall.sys.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 系统-关联角色数据表(SysRoleRelation)表更新数据模型类
 *
 * @author yunze
 * @since 2024-11-17 19:56:00
 */
@Data
public class SysRoleRelationUpdateDto implements Serializable {

    private static final long serialVesionUID = 1L;

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

