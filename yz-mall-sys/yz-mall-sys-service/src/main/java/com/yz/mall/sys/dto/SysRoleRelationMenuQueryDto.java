package com.yz.mall.sys.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 系统-角色关联菜单表(SysRoleRelationMenu)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-11-28 12:58:06
 */
@Data
public class SysRoleRelationMenuQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色Id
     */
    @NotNull(message = "角色Id不能为空")
    private Long roleId;

    /**
     * 菜单Id
     */
    private Long menuId;
}

