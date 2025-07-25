package com.yz.mall.auth.service;

import com.yz.mall.auth.dto.AuthRolePermissionQueryDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * 权限操作接口
 *
 * @author yunze
 * @since 2025/7/24 11:45
 */
public interface ExtendPermissionService {

    /**
     * 获取指定角色，指定菜单类型所拥有的权限（本操作不缓存信息）
     *
     * @param query 用户拥有的角色Id和查询的菜单权限类型
     * @return 权限标识 <角色Id，List<权限标识>>
     */
    Map<String, List<String>> getPermissionsByRoleIds(@Valid AuthRolePermissionQueryDto query);
}
