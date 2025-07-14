package com.yz.mall.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统-角色关联菜单表(SysRoleRelationMenu)表更新数据模型类
 *
 * @author yunze
 * @since 2024-11-28 12:58:06
 */
@Data
public class SysRoleRelationMenuUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 角色Id
     */
    private Long roleId;

    /**
     * 菜单Id
     */
    private Long menuId;


}

