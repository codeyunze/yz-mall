package com.yz.mall.sys.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.SysMenuQueryDto;
import com.yz.mall.sys.dto.SysUserQueryDto;
import com.yz.mall.sys.entity.SysMenu;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.mapper.SysUserMapper;
import com.yz.mall.sys.service.SysMenuService;
import com.yz.mall.sys.service.SysRoleRelationMenuService;
import com.yz.mall.sys.service.SysUserRelationRoleService;
import com.yz.mall.sys.service.SysUserService;
import com.yz.mall.sys.vo.MenuByRoleVo;
import com.yz.mall.sys.vo.SysTreeMenuVo;
import com.yz.mall.sys.vo.SysUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yunze
 * @since 2025/7/10 10:51
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysMenuService sysMenuService;

    private final SysUserRelationRoleService sysUserRelationRoleService;

    private final SysRoleRelationMenuService sysRoleRelationMenuService;

    public SysUserServiceImpl(SysMenuService sysMenuService, SysUserRelationRoleService sysUserRelationRoleService, SysRoleRelationMenuService sysRoleRelationMenuService) {
        this.sysMenuService = sysMenuService;
        this.sysUserRelationRoleService = sysUserRelationRoleService;
        this.sysRoleRelationMenuService = sysRoleRelationMenuService;
    }

    @Override
    public Page<SysUserVo> page(long current, long size, SysUserQueryDto filter) {
        log.info("用户信息实现类");
        return baseMapper.selectPage(new Page<>(current, size), filter);
    }

    @Override
    public List<SysTreeMenuVo> getUserMenus(Long userId) {
        List<Long> roleIds = sysUserRelationRoleService.getRoleIdsByRelationId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        // 菜单Id列表
        List<Long> menuIds = sysRoleRelationMenuService.getMenuIdsByRoleIds(roleIds);
        // 查询指定菜单的详细信息
        SysMenuQueryDto menuQueryDto = new SysMenuQueryDto();
        menuQueryDto.setMenuIds(menuIds);
        List<SysMenu> menus = sysMenuService.list(menuQueryDto);

        // 获取各菜单所对应可以访问的角色Id
        List<MenuByRoleVo> roleByMenuList = sysRoleRelationMenuService.getRoleIdByMenuIds(menuIds);
        // Map<菜单Id, List<角色Id>>
        Map<Long, List<Long>> roleByMenuMap = roleByMenuList.stream()
                .collect(Collectors.groupingBy(
                        MenuByRoleVo::getMenuId,
                        Collectors.mapping(MenuByRoleVo::getRoleId, Collectors.toList())
                ));

        // 解析菜单信息
        return sysMenuService.menusInfoProcessor(menus, 0L, roleByMenuMap);
    }
}
