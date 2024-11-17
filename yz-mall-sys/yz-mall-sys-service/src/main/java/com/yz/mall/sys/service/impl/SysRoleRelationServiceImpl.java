package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.SysRoleRelationAddDto;
import com.yz.mall.sys.dto.SysRoleRelationQueryDto;
import com.yz.mall.sys.dto.SysRoleRelationUpdateDto;
import com.yz.mall.sys.mapper.SysRoleRelationMapper;
import com.yz.mall.sys.entity.SysRoleRelation;
import com.yz.mall.sys.service.SysRoleRelationService;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 系统-关联角色数据表(SysRoleRelation)表服务实现类
 *
 * @author yunze
 * @since 2024-11-17 19:55:59
 */
@Service
public class SysRoleRelationServiceImpl extends ServiceImpl<SysRoleRelationMapper, SysRoleRelation> implements SysRoleRelationService {

    @Override
    public Long save(SysRoleRelationAddDto dto) {
        SysRoleRelation bo = new SysRoleRelation();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysRoleRelationUpdateDto dto) {
        SysRoleRelation bo = new SysRoleRelation();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysRoleRelation> page(PageFilter<SysRoleRelationQueryDto> filter) {
        LambdaQueryWrapper<SysRoleRelation> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

