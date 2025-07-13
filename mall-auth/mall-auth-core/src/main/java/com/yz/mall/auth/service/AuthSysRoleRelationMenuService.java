package com.yz.mall.auth.service;


import com.yz.mall.auth.dto.InternalRolePermissionQueryDto;

import java.util.List;
import java.util.Map;

/**
 * 内部开放接口: 系统管理-角色菜单信息
 *
 * @author yunze
 * @date 2024/12/9 星期一 12:44
 */
public interface AuthSysRoleRelationMenuService {

    /**
     * 获取指定角色所拥有的按钮权限
     *
     * @param query 用户拥有的角色Id和查询的菜单权限类型
     * @return 按钮权限标识 <角色Id，List<权限标识>>
     */
    Map<String, List<String>> getPermissionsByRoleIds(InternalRolePermissionQueryDto query);

}
