package com.yz.mall.sys.vo;

import lombok.Data;

/**
 * 拥有菜单的角色Id
 *
 * @author yunze
 * @since 2025/5/18 17:00
 */
@Data
public class MenuByRoleVo {

    /**
     * 角色Id
     */
    private Long roleId;

    /**
     * 菜单Id
     */
    private Long menuId;
}
