package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.enums.MenuTypeEnum;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.redis.RedisCacheKey;
import com.yz.mall.sys.dto.SysRolePermissionDto;
import com.yz.mall.sys.dto.SysRoleRelationMenuBindDto;
import com.yz.mall.sys.dto.SysRoleRelationMenuQueryDto;
import com.yz.mall.sys.entity.SysRoleRelationMenu;
import com.yz.mall.sys.mapper.SysRoleRelationMenuMapper;
import com.yz.mall.sys.service.RefreshPermission;
import com.yz.mall.sys.service.SysRoleRelationMenuService;
import com.yz.mall.sys.vo.MenuByRoleVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统-角色关联菜单表(SysRoleRelationMenu)表服务实现类
 *
 * @author yunze
 * @since 2024-11-28 12:58:05
 */
@Service
public class SysRoleRelationMenuServiceImpl extends ServiceImpl<SysRoleRelationMenuMapper, SysRoleRelationMenu> implements SysRoleRelationMenuService {

    private final RedisTemplate<String, Object> defaultRedisTemplate;

    private final RefreshPermission refreshPermission;

    public SysRoleRelationMenuServiceImpl(RedisTemplate<String, Object> defaultRedisTemplate, RefreshPermission refreshPermission) {
        this.defaultRedisTemplate = defaultRedisTemplate;
        this.refreshPermission = refreshPermission;
    }

    @Override
    public List<SysRoleRelationMenu> list(SysRoleRelationMenuQueryDto filter) {
        LambdaQueryWrapper<SysRoleRelationMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(filter.getRoleId() != null, SysRoleRelationMenu::getRoleId, filter.getRoleId());
        queryWrapper.eq(filter.getMenuId() != null, SysRoleRelationMenu::getMenuId, filter.getMenuId());
        return baseMapper.selectList(queryWrapper);
    }

    @DS("slave")
    @Override
    public List<String> getMenuIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleRelationMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysRoleRelationMenu::getMenuId);
        queryWrapper.eq(SysRoleRelationMenu::getRoleId, roleId);
        List<SysRoleRelationMenu> menus = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        // TODO: 2024/12/4 yunze 添加缓存操作，且在数据更新时需要清理缓存
        return menus.stream().map(t -> String.valueOf(t.getMenuId())).collect(Collectors.toList());
    }

    @DS("slave")
    @Override
    public List<Long> getMenuIdsByRoleIds(List<Long> roleIds) {
        LambdaQueryWrapper<SysRoleRelationMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysRoleRelationMenu::getMenuId);
        queryWrapper.in(SysRoleRelationMenu::getRoleId, roleIds);
        List<SysRoleRelationMenu> menus = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        // TODO: 2024/12/5 yunze 添加缓存操作，且在数据更新时需要清理缓存
        return menus.stream().map(SysRoleRelationMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> getPermissionsAndCacheByRoleIds(MenuTypeEnum menuType, List<Long> roleIds) {
        if (!MenuTypeEnum.BUTTON.equals(menuType) && !MenuTypeEnum.API.equals(menuType)) {
            return Collections.emptyMap();
        }

        // 最终汇总结果
        Map<String, List<String>> result = new HashMap<>();

        // 缓存里没有需要去数据库查询权限的角色Id
        List<Long> needQueryRoleIds = new ArrayList<>();
        for (Long roleId : roleIds) {
            // 先从缓存查询角色对应的按钮权限
            String cacheKey = RedisCacheKey.permission(menuType.name(), String.valueOf(roleId));
            // 判断缓存里是否存在该角色的权限
            if (Boolean.FALSE.equals(defaultRedisTemplate.hasKey(cacheKey))) {
                // 没有缓存权限信息的角色
                needQueryRoleIds.add(roleId);
                continue;
            }

            // 从缓存里获取到角色权限
            List<Object> permissionsByRoleId = defaultRedisTemplate.boundListOps(cacheKey).range(0, -1);
            if (CollectionUtils.isEmpty(permissionsByRoleId)) {
                // 缓存里存的按钮权限为空，是为了防止出现缓存穿透问题
                continue;
            }

            result.put(String.valueOf(roleId), permissionsByRoleId.stream().map(Object::toString).collect(Collectors.toList()));
        }

        if (CollectionUtils.isEmpty(needQueryRoleIds)) {
            return result;
        }

        // 从数据库获取到各个角色的权限
        List<SysRolePermissionDto> list;
        if (MenuTypeEnum.BUTTON.equals(menuType)) {
            // 按钮权限
            list = baseMapper.selectPermissionsButtonByRoleIds(needQueryRoleIds);
        } else {
            // 接口权限
            list = baseMapper.selectPermissionsApiByRoleIds(needQueryRoleIds);
        }

        if (CollectionUtils.isEmpty(list)) {
            // 需要查库获取权限的角色
            needQueryRoleIds.forEach(roleId -> defaultRedisTemplate.opsForList().rightPush(RedisCacheKey.permission(menuType.name(), String.valueOf(roleId)), Collections.emptyList()));
            return result;
        }

        Map<String, List<String>> collect = list.stream().collect(
                Collectors.groupingBy(
                        SysRolePermissionDto::getRoleId,
                        Collectors.mapping(SysRolePermissionDto::getAuths, Collectors.toList())
                ));

        // 权限信息存入缓存
        collect.forEach((key, value) -> value.forEach(permission -> defaultRedisTemplate.opsForList().rightPush(RedisCacheKey.permission(menuType.name(), String.valueOf(key)), permission)));

        result.putAll(collect);
        return result;
    }

    @DS("slave")
    @Override
    public List<MenuByRoleVo> getRoleIdByMenuIds(List<Long> menuIds) {
        LambdaQueryWrapper<SysRoleRelationMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysRoleRelationMenu::getMenuId, SysRoleRelationMenu::getRoleId);
        queryWrapper.in(SysRoleRelationMenu::getMenuId, menuIds);
        List<SysRoleRelationMenu> list = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        
        // 转换为MenuByRoleVo并去重
        return list.stream()
            .map(item -> {
                MenuByRoleVo vo = new MenuByRoleVo();
                vo.setMenuId(item.getMenuId());
                vo.setRoleId(item.getRoleId());
                return vo;
            })
            .distinct()
            .collect(Collectors.toList());
    }


    @Override
    public boolean bind(SysRoleRelationMenuBindDto dto) {
        // 查询出角色拥有所有菜单
        List<String> alreadyBindMenuIds = this.getMenuIdsByRoleId(dto.getRoleId());
        // 归类出角色已经拥有的哪些菜单是需要去除的
        List<Long> removeMenuIds = alreadyBindMenuIds.stream().filter(menuId -> !dto.getMenuIds().contains(Long.parseLong(menuId))).map(Long::parseLong).collect(Collectors.toList());
        // 归类出哪些菜单是新分配给角色的
        List<Long> bindMenuIds = dto.getMenuIds().stream().filter(menuId -> !alreadyBindMenuIds.contains(String.valueOf(menuId))).collect(Collectors.toList());

        // 删除需要去除的角色关联信息
        if (!CollectionUtils.isEmpty(removeMenuIds)) {
            LambdaUpdateWrapper<SysRoleRelationMenu> removeWrapper = new LambdaUpdateWrapper<>();
            removeWrapper.eq(SysRoleRelationMenu::getRoleId, dto.getRoleId())
                    .in(SysRoleRelationMenu::getMenuId, removeMenuIds);
            if (baseMapper.delete(removeWrapper) <= 0) {
                throw new BusinessException("角色绑定菜单权限操作失败");
            }
        }

        // 新增新分配的角色&菜单关联信息
        if (CollectionUtils.isEmpty(bindMenuIds)) {
            // 清理角色的按钮和接口缓存
            refreshPermission.refreshButtonPermissionCache();
            refreshPermission.refreshApiPermissionCache();
            return true;
        }
        List<SysRoleRelationMenu> addInfos = new ArrayList<>();
        bindMenuIds.forEach(menuId -> {
            SysRoleRelationMenu info = new SysRoleRelationMenu();
            info.setId(IdUtil.getSnowflakeNextId());
            info.setRoleId(dto.getRoleId());
            info.setMenuId(menuId);
            addInfos.add(info);
        });

        boolean saved = super.saveBatch(addInfos);

        // 清理角色的按钮和接口缓存
        refreshPermission.refreshButtonPermissionCache();
        refreshPermission.refreshApiPermissionCache();
        return saved;
    }
}

