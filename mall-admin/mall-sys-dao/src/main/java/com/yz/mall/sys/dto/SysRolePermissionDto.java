package com.yz.mall.sys.dto;

import com.yz.mall.sys.entity.SysMenu;
import com.yz.mall.sys.entity.SysRole;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色关联菜单权限标识
 *
 * @author yunze
 * @since 2024-12-09 12:58:05
 */
@Data
public class SysRolePermissionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色Id {@link SysRole#getId()}
     */
    private String roleId;

    /**
     * 权限标识 {@link SysMenu#getAuths()}
     */
    private String auths;
}

