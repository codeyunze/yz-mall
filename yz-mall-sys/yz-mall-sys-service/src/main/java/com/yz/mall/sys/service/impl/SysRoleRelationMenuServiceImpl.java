package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.SysRoleRelationMenuAddDto;
import com.yz.mall.sys.dto.SysRoleRelationMenuQueryDto;
import com.yz.mall.sys.dto.SysRoleRelationMenuUpdateDto;
import com.yz.mall.sys.mapper.SysRoleRelationMenuMapper;
import com.yz.mall.sys.entity.SysRoleRelationMenu;
import com.yz.mall.sys.service.SysRoleRelationMenuService;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

