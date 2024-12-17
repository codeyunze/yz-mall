package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysRoleRelationMenuBindDto;
import com.yz.mall.sys.dto.SysRoleRelationMenuQueryDto;
import com.yz.mall.sys.entity.SysRole;
import com.yz.mall.sys.entity.SysRoleRelationMenu;
import com.yz.mall.sys.enums.MenuTypeEnum;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 系统-角色关联菜单表(SysRoleRelationMenu)表服务接口
 *
 * @author yunze
 * @since 2024-11-28 12:58:05
 */
public interface SysRoleRelationMenuService extends IService<SysRoleRelationMenu> {

    /**
     * 列表查询
     *
     * @param filter 过滤条件
     * @return 查询列表数据
     */
    List<SysRoleRelationMenu> list(SysRoleRelationMenuQueryDto filter);

    /**
     * 获取指定角色所拥有的所有菜单Id
     *
     * @param roleId 指定的角色Id
     * @return 菜单Id
     */
    List<String> getMenuIdsByRoleId(Long roleId);

    /**
     * 获取指定角色列表所拥有的所有菜单Id
     *
     * @param roleIds 指定的角色Id
     * @return 菜单Id列表
     */
    List<Long> getMenuIdsByRoleIds(List<Long> roleIds);

    /**
     * 获取指定角色所拥有的权限,并进行缓存
     *
     * @param menuType 菜单类型
     * @param roleIds  用户拥有的角色Id {@link SysRole#getOrgId()}
     * @return 权限标识 <角色Id，List<权限标识>>
     */
    Map<String, List<String>> getPermissionsAndCacheByRoleIds(MenuTypeEnum menuType, @NotNull List<Long> roleIds);

    /**
     * 角色绑定菜单
     *
     * @param dto 绑定基础信息
     * @return 是否绑定成功
     */
    boolean bind(SysRoleRelationMenuBindDto dto);
}

