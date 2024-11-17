package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.SysPermissionAddDto;
import com.yz.mall.sys.dto.SysPermissionQueryDto;
import com.yz.mall.sys.dto.SysPermissionUpdateDto;
import com.yz.mall.sys.mapper.SysPermissionMapper;
import com.yz.mall.sys.entity.SysPermission;
import com.yz.mall.sys.service.SysPermissionService;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 系统-权限数据表(SysPermission)表服务实现类
 *
 * @author yunze
 * @since 2024-11-17 20:08:25
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Override
    public Long save(SysPermissionAddDto dto) {
        SysPermission bo = new SysPermission();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysPermissionUpdateDto dto) {
        SysPermission bo = new SysPermission();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysPermission> page(PageFilter<SysPermissionQueryDto> filter) {
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

