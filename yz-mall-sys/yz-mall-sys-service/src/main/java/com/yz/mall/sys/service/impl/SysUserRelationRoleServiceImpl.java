package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.SysUserRelationRoleAddDto;
import com.yz.mall.sys.dto.SysUserRelationRoleQueryDto;
import com.yz.mall.sys.dto.SysUserRelationRoleUpdateDto;
import com.yz.mall.sys.mapper.SysUserRelationRoleMapper;
import com.yz.mall.sys.entity.SysUserRelationRole;
import com.yz.mall.sys.service.SysUserRelationRoleService;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统-用户与组织关联角色表(SysUserRelationRole)表服务实现类
 *
 * @author yunze
 * @since 2024-11-26 11:46:13
 */
@Service
public class SysUserRelationRoleServiceImpl extends ServiceImpl<SysUserRelationRoleMapper, SysUserRelationRole> implements SysUserRelationRoleService {

    @Override
    public Long save(SysUserRelationRoleAddDto dto) {
        SysUserRelationRole bo = new SysUserRelationRole();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysUserRelationRoleUpdateDto dto) {
        SysUserRelationRole bo = new SysUserRelationRole();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysUserRelationRole> page(PageFilter<SysUserRelationRoleQueryDto> filter) {
        LambdaQueryWrapper<SysUserRelationRole> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @Override
    public List<Long> getRoleIdsByRelationId(Long relationId) {
        LambdaQueryWrapper<SysUserRelationRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysUserRelationRole::getRoleId);
        queryWrapper.eq(SysUserRelationRole::getRelationId, relationId);
        List<SysUserRelationRole> userRelationRoleList = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userRelationRoleList)) {
            return Collections.emptyList();
        }
        return userRelationRoleList.stream().map(SysUserRelationRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    public List<String> getRoleCodesByRelationId(Long relationId) {
        return baseMapper.selectRoleCodesByRelationId(relationId);
    }
}

