package com.yz.mall.sys.impl;

import com.yz.mall.sys.dto.InternalRolePermissionQueryDto;
import com.yz.mall.sys.service.InternalSysRoleRelationMenuService;
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
public class InternalSysRoleRelationMenuServiceImpl implements InternalSysRoleRelationMenuService {

    private final SysRoleRelationMenuService sysRoleRelationMenuService;

    public InternalSysRoleRelationMenuServiceImpl(SysRoleRelationMenuService sysRoleRelationMenuService) {
        this.sysRoleRelationMenuService = sysRoleRelationMenuService;
    }

    @Override
    public Map<String, List<String>> getPermissionsByRoleIds(InternalRolePermissionQueryDto query) {
        return sysRoleRelationMenuService.getPermissionsByRoleIds(query.getMenuType(), query.getRoleIds());
    }
}
