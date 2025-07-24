package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.auth.dto.AuthRolePermissionQueryDto;
import com.yz.mall.auth.service.ExtendPermissionService;
import com.yz.mall.base.enums.EnableEnum;
import com.yz.mall.base.enums.MenuTypeEnum;
import com.yz.mall.base.exception.DataNotExistException;
import com.yz.mall.redis.RedisCacheKey;
import com.yz.mall.sys.dto.SysMenuAddDto;
import com.yz.mall.sys.dto.SysMenuQueryDto;
import com.yz.mall.sys.dto.SysMenuUpdateDto;
import com.yz.mall.sys.entity.SysMenu;
import com.yz.mall.sys.mapper.SysMenuMapper;
import com.yz.mall.sys.service.RefreshPermission;
import com.yz.mall.sys.service.SysMenuService;
import com.yz.mall.sys.service.SysRoleService;
import com.yz.mall.sys.vo.SysMenuSlimVo;
import com.yz.mall.sys.vo.SysTreeMenuMetaVo;
import com.yz.mall.sys.vo.SysTreeMenuVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 系统-菜单资源表(SysMenu)表服务实现类
 *
 * @author yunze
 * @since 2024-11-21 23:29:02
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final RefreshPermission refreshPermission;

    private final SysRoleService roleService;

    private final ExtendPermissionService extendPermissionService;

    private final RedisTemplate<String, Object> defaultRedisTemplate;

    public SysMenuServiceImpl(RefreshPermission refreshPermission
            , ExtendPermissionService extendPermissionService
            , SysRoleService roleService
            , RedisTemplate<String, Object> defaultRedisTemplate) {
        this.refreshPermission = refreshPermission;
        this.extendPermissionService = extendPermissionService;
        this.roleService = roleService;
        this.defaultRedisTemplate = defaultRedisTemplate;
    }

    @Override
    public Long save(SysMenuAddDto dto) {
        // 新增菜单信息
        SysMenu bo = new SysMenu();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setMenuType(dto.getMenuType().get());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysMenuUpdateDto dto) {
        SysMenu menu = baseMapper.selectById(dto.getId());
        if (menu == null) {
            throw new DataNotExistException("菜单数据不存在");
        }

        // 更新菜单信息
        SysMenu bo = new SysMenu();
        BeanUtils.copyProperties(dto, bo);
        boolean flag = baseMapper.updateById(bo) > 0;

        // 清理角色的按钮和接口缓存
        refreshPermission.refreshButtonPermissionCache();
        refreshPermission.refreshApiPermissionCache();

        // 获取系统所有有效的角色
        /*List<Long> roleIds = roleService.getEffectiveRoleIds();
        if (CollectionUtils.isEmpty(roleIds)) {
            return flag;
        }

        // 缓存角色的按钮权限
        Map<String, List<String>> permissionsBtnByRoleIds = extendPermissionService.getPermissionsByRoleIds(new AuthRolePermissionQueryDto(MenuTypeEnum.BUTTON, roleIds));
        if (!CollectionUtils.isEmpty(permissionsBtnByRoleIds)) {
            permissionsBtnByRoleIds.forEach((roleId, btnRoles) -> {
                String cacheKey = RedisCacheKey.permission(MenuTypeEnum.BUTTON.name(), roleId);
                if (!CollectionUtils.isEmpty(btnRoles)) {
                    btnRoles.forEach(btnRole -> {
                        defaultRedisTemplate.opsForList().rightPush(cacheKey, btnRole);
                        defaultRedisTemplate.expire(cacheKey, 86400, TimeUnit.SECONDS);
                    });
                }
            });
        }

        // 缓存角色的接口权限
        Map<String, List<String>> permissionsApiByRoleIds = extendPermissionService.getPermissionsByRoleIds(new AuthRolePermissionQueryDto(MenuTypeEnum.API, roleIds));
        if (!CollectionUtils.isEmpty(permissionsApiByRoleIds)) {
            permissionsApiByRoleIds.forEach((roleId, apiRoles) -> {
                String cacheKey = RedisCacheKey.permission(MenuTypeEnum.API.name(), String.valueOf(roleId));
                if (!CollectionUtils.isEmpty(apiRoles)) {
                    apiRoles.forEach(apiRole -> {
                        defaultRedisTemplate.opsForList().rightPush(cacheKey, apiRole);
                        defaultRedisTemplate.expire(cacheKey, 86400, TimeUnit.SECONDS);
                    });
                }
            });
        }*/

        return flag;
    }

    @Override
    public List<SysMenu> list(SysMenuQueryDto filter) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(!CollectionUtils.isEmpty(filter.getMenuIds()), SysMenu::getId, filter.getMenuIds());
        queryWrapper.orderByAsc(SysMenu::getParentId).orderByAsc(SysMenu::getMenuType).orderByAsc(SysMenu::getSort);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<SysMenuSlimVo> listSlim() {
        return baseMapper.selectMenusSlim();
    }

    @Override
    public List<SysTreeMenuVo> menusInfoProcessor(List<SysMenu> menus, Long parentId, Map<Long, List<Long>> roleByMenuMap) {
        List<SysMenu> localMenus = menus.stream().filter(menu -> menu.getParentId().equals(parentId)).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(localMenus)) {
            return Collections.emptyList();
        }

        List<SysTreeMenuVo> treeMenuVos = new ArrayList<>();
        for (int i = 0; i < localMenus.size(); i++) {
            SysMenu menu = localMenus.get(i);
            if (MenuTypeEnum.BUTTON.get().equals(menu.getMenuType())) {
                continue;
            }

            SysTreeMenuVo vo = new SysTreeMenuVo();
            SysTreeMenuMetaVo metaVo = new SysTreeMenuMetaVo();

            // 公共属性
            vo.setPath(menu.getPath());
            metaVo.setIcon(menu.getIcon());
            metaVo.setTitle(menu.getTitle());
            // metaVo.setFrameSrc(menu.getFrameSrc());
            // metaVo.setKeepAlive(EnableEnum.ENABLE.get().equals(menu.getKeepAlive()));
            metaVo.setShowLink(EnableEnum.ENABLE.get().equals(menu.getShowLink()));
            // metaVo.setActivePath(menu.getActivePath());
            metaVo.setShowParent(EnableEnum.ENABLE.get().equals(menu.getShowParent()));

            if (roleByMenuMap.containsKey(menu.getId()) && !CollectionUtils.isEmpty(roleByMenuMap.get(menu.getId()))) {
                metaVo.setRoles(roleByMenuMap.get(menu.getId()));
            }

            if (0L != parentId) {
                vo.setName(menu.getName());
            } else {
                metaVo.setRank(menu.getSort());
            }

            // 不同类型菜单设置
            if (MenuTypeEnum.IFRAME.get().equals(menu.getMenuType())) {
                metaVo.setFrameSrc(menu.getFrameSrc());
                metaVo.setKeepAlive(EnableEnum.ENABLE.get().equals(menu.getKeepAlive()));
            } else if (MenuTypeEnum.LINK.get().equals(menu.getMenuType())) {
                vo.setName(menu.getName());
            }

            vo.setChildren(this.menusInfoProcessor(menus, menu.getId(), roleByMenuMap));

            // 如果一级菜单没有子菜单，则需要做如下特殊处理
            if (CollectionUtils.isEmpty(vo.getChildren()) && 0L == parentId) {
                vo.setChildren(Collections.singletonList(firstLevelMenuProcessor(menu)));
            }

            vo.setMeta(metaVo);
            treeMenuVos.add(vo);
        }
        return treeMenuVos;
    }

    /**
     * 一级菜单处理
     * @param menu 菜单信息
     * @return 处理后的菜单路由信息
     */
    private static SysTreeMenuVo firstLevelMenuProcessor(SysMenu menu) {
        SysTreeMenuVo children = new SysTreeMenuVo();
        SysTreeMenuMetaVo childrenMetaVo = new SysTreeMenuMetaVo();

        children.setPath(menu.getPath());
        children.setName(menu.getName());
        children.setComponent(menu.getComponent());
        childrenMetaVo.setTitle(menu.getTitle());
        childrenMetaVo.setIcon(menu.getIcon());
        childrenMetaVo.setExtraIcon(menu.getExtraIcon());
        children.setMeta(childrenMetaVo);
        return children;
    }
}

