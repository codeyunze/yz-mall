package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.enums.EnableEnum;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.base.exception.DataNotExistException;
import com.yz.mall.sys.SysProperties;
import com.yz.mall.sys.dto.SysRoleAddDto;
import com.yz.mall.sys.dto.SysRoleQueryDto;
import com.yz.mall.sys.dto.SysRoleUpdateDto;
import com.yz.mall.sys.entity.SysRole;
import com.yz.mall.sys.mapper.SysRoleMapper;
import com.yz.mall.sys.service.SysRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 系统-角色数据表(SysRole)表服务实现类
 *
 * @author yunze
 * @since 2024-11-17 18:15:15
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysProperties sysProperties;

    public SysRoleServiceImpl(SysProperties sysProperties) {
        this.sysProperties = sysProperties;
    }

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
    public boolean updateRoleStatusById(Long id) {
        if (sysProperties.getSuperAdminRoleId().equals(id.toString())) {
            throw new BusinessException("禁止操作默认数据");
        }
        SysRole role = baseMapper.selectById(id);
        if (role ==null){
            throw new DataNotExistException();
        }

        role.setStatus(Objects.equals(EnableEnum.ENABLE.get(), role.getStatus()) ? EnableEnum.Disable.get() : EnableEnum.ENABLE.get());
        return baseMapper.updateById(role) > 0;
    }

    // @DS("slave")
    @Override
    public Page<SysRole> page(PageFilter<SysRoleQueryDto> filter) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @DS("slave")
    @Override
    public List<SysRole> list(SysRoleQueryDto filter) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(filter.getRoleName()), SysRole::getRoleName, filter.getRoleName());
        queryWrapper.eq(StringUtils.hasText(filter.getRoleCode()), SysRole::getRoleCode, filter.getRoleCode());
        return baseMapper.selectList(queryWrapper);
    }

    @DS("slave")
    @Override
    public List<String> getRoleMenusByRoleId(Long roleId) {
        return Collections.emptyList();
    }

    @DS("slave")
    @Override
    public List<Long> getEffectiveRoleIds() {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        List<SysRole> roles = baseMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(roles)) {
            return roles.stream().map(SysRole::getId).toList();
        }
        return Collections.emptyList();
    }
}

