package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.dto.ExtendRolePermissionQueryDto;
import com.yz.mall.sys.service.ExtendSysRoleRelationMenuService;
import com.yz.mall.sys.service.SysRoleRelationMenuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 内部暴露service实现类: 角色关联菜单信息
 *
 * @author yunze
 * @date 2024/12/9 16:48
 */
@Service
public class ExtendSysRoleRelationMenuServiceImpl implements ExtendSysRoleRelationMenuService {

    private final SysRoleRelationMenuService sysRoleRelationMenuService;

    public ExtendSysRoleRelationMenuServiceImpl(SysRoleRelationMenuService sysRoleRelationMenuService) {
        this.sysRoleRelationMenuService = sysRoleRelationMenuService;
    }

    @Override
    public Map<String, List<String>> getPermissionsByRoleIds(ExtendRolePermissionQueryDto query) {
        return sysRoleRelationMenuService.getPermissionsAndCacheByRoleIds(query.getMenuType(), query.getRoleIds());
    }
}
