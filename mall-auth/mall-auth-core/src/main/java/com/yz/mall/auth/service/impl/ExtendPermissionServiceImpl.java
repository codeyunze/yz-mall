package com.yz.mall.auth.service.impl;

import com.yz.mall.auth.dto.AuthRolePermissionQueryDto;
import com.yz.mall.auth.service.AuthSysRoleRelationMenuService;
import com.yz.mall.auth.service.ExtendPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author yunze
 * @since 2025/7/24 11:50
 */
@Service
public class ExtendPermissionServiceImpl implements ExtendPermissionService {

    private final AuthSysRoleRelationMenuService authSysRoleRelationMenuService;

    public ExtendPermissionServiceImpl(AuthSysRoleRelationMenuService authSysRoleRelationMenuService) {
        this.authSysRoleRelationMenuService = authSysRoleRelationMenuService;
    }

    @Override
    public Map<String, List<String>> getPermissionsByRoleIds(AuthRolePermissionQueryDto query) {
        return authSysRoleRelationMenuService.getPermissionsByRoleIds(query);
    }
}
