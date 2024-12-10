package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.advice.exception.BusinessException;
import com.yz.mall.sys.dto.*;
import com.yz.mall.sys.entity.SysRoleRelationMenu;
import com.yz.mall.sys.mapper.SysRoleRelationMenuMapper;
import com.yz.mall.sys.service.SysRoleRelationMenuService;
import org.springframework.beans.BeanUtils;
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

    @Override
    public Long save(SysRoleRelationMenuAddDto dto) {
        SysRoleRelationMenu bo = new SysRoleRelationMenu();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);

        return bo.getId();
    }

    @Override
    public boolean update(SysRoleRelationMenuUpdateDto dto) {
        SysRoleRelationMenu bo = new SysRoleRelationMenu();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
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
    public Map<String, List<String>> getPermissionsByRoleIds(List<Long> roleIds) {
        List<SysRolePermissionDto> list = baseMapper.selectPermissionsByRoleIds(roleIds);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(
                Collectors.groupingBy(
                        SysRolePermissionDto::getRoleId,
                        Collectors.mapping(SysRolePermissionDto::getAuths, Collectors.toList())
                ));
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
        return super.saveBatch(addInfos);
    }
}

