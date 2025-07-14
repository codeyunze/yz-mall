package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.enums.MenuTypeEnum;
import com.yz.mall.sys.dto.SysRolePermissionDto;
import com.yz.mall.sys.entity.SysRole;
import com.yz.mall.sys.entity.SysRoleRelationMenu;
import com.yz.mall.sys.mapper.SysRoleRelationMenuMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yunze
 * @date 2024/12/15 星期日 15:02
 */
@Component
public class PermissionService extends ServiceImpl<SysRoleRelationMenuMapper, SysRoleRelationMenu> {

    /**
     * 获取指定角色所拥有的权限
     *
     * @param menuType 菜单类型
     * @param roleIds  用户拥有的角色Id {@link SysRole#getOrgId()}
     * @return 权限标识 <角色Id，List<权限标识>>
     */
    public Map<String, List<String>> getPermissionsByRoleIds(MenuTypeEnum menuType, List<Long> roleIds) {
        if (!MenuTypeEnum.BUTTON.equals(menuType) && !MenuTypeEnum.API.equals(menuType)) {
            return Collections.emptyMap();
        }

        // 从数据库获取到各个角色的权限
        List<SysRolePermissionDto> list;
        if (MenuTypeEnum.BUTTON.equals(menuType)) {
            // 按钮权限
            list = baseMapper.selectPermissionsButtonByRoleIds(roleIds);
        } else {
            // 接口权限
            list = baseMapper.selectPermissionsApiByRoleIds(roleIds);
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
