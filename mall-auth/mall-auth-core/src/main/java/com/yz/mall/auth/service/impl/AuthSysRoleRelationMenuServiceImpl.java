package com.yz.mall.auth.service.impl;

import com.yz.mall.auth.dto.AuthRolePermissionQueryDto;
import com.yz.mall.auth.service.AuthSysRoleRelationMenuService;
import com.yz.mall.base.enums.MenuTypeEnum;
import com.yz.mall.sys.dto.SysRolePermissionDto;
import com.yz.mall.sys.mapper.SysRoleRelationMenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yunze
 * @since 2025/7/13 08:44
 */
@Service
public class AuthSysRoleRelationMenuServiceImpl implements AuthSysRoleRelationMenuService {

    private final SysRoleRelationMenuMapper sysRoleRelationMenuMapper;

    public AuthSysRoleRelationMenuServiceImpl(SysRoleRelationMenuMapper sysRoleRelationMenuMapper) {
        this.sysRoleRelationMenuMapper = sysRoleRelationMenuMapper;
    }

    @Override
    public Map<String, List<String>> getPermissionsByRoleIds(AuthRolePermissionQueryDto query) {
        if (!MenuTypeEnum.BUTTON.equals(query.getMenuType()) && !MenuTypeEnum.API.equals(query.getMenuType())) {
            return Collections.emptyMap();
        }

        // 从数据库获取到各个角色的权限
        List<SysRolePermissionDto> list;
        if (MenuTypeEnum.BUTTON.equals(query.getMenuType())) {
            // 按钮权限
            list = sysRoleRelationMenuMapper.selectPermissionsButtonByRoleIds(query.getRoleIds());
        } else {
            // 接口权限
            list = sysRoleRelationMenuMapper.selectPermissionsApiByRoleIds(query.getRoleIds());
        }

        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }

        return list.stream().collect(
                Collectors.groupingBy(
                        SysRolePermissionDto::getRoleId,
                        Collectors.mapping(SysRolePermissionDto::getAuths, Collectors.toList())
                ));
    }

}
