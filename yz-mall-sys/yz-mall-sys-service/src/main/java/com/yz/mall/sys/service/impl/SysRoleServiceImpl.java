package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.SysRoleAddDto;
import com.yz.mall.sys.dto.SysRoleQueryDto;
import com.yz.mall.sys.dto.SysRoleUpdateDto;
import com.yz.mall.sys.mapper.SysRoleMapper;
import com.yz.mall.sys.entity.SysRole;
import com.yz.mall.sys.service.SysRoleService;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 系统-角色数据表(SysRole)表服务实现类
 *
 * @author yunze
 * @since 2024-11-17 18:15:15
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public Long save(SysRoleAddDto dto) {
        SysRole bo = new SysRole();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysRoleUpdateDto dto) {
        SysRole bo = new SysRole();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysRole> page(PageFilter<SysRoleQueryDto> filter) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

