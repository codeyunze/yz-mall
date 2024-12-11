package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.dto.SysRolePermissionDto;
import com.yz.mall.sys.entity.SysMenu;
import com.yz.mall.sys.entity.SysRole;
import com.yz.mall.sys.entity.SysRoleRelationMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统-角色关联菜单表(SysRoleRelationMenu)表数据库访问层
 *
 * @author yunze
 * @since 2024-11-28 12:58:05
 */
@Mapper
public interface SysRoleRelationMenuMapper extends BaseMapper<SysRoleRelationMenu> {

    /**
     * 获取指定角色所拥有的按钮权限
     *
     * @param roleIds  用户拥有的角色Id {@link SysRole#getOrgId()}
     * @return 按钮权限标识 {@link SysMenu#getAuths()}
     */
    List<SysRolePermissionDto> selectPermissionsButtonByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 获取指定角色所拥有的接口权限
     *
     * @param roleIds 用户拥有的角色Id {@link SysRole#getOrgId()}
     * @return 接口权限标识 {@link SysMenu#getAuths()}
     */
    List<SysRolePermissionDto> selectPermissionsApiByRoleIds(@Param("roleIds") List<Long> roleIds);

}

