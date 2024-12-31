package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.SysUserRelationOrgAddDto;
import com.yz.mall.sys.dto.SysUserRelationOrgQueryDto;
import com.yz.mall.sys.dto.SysUserRelationOrgUpdateDto;
import com.yz.mall.sys.entity.SysUserRelationOrg;
import com.yz.mall.sys.mapper.SysUserRelationOrgMapper;
import com.yz.mall.sys.service.SysUserRelationOrgService;
import com.yz.mall.web.common.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统-用户关联组织数据表(SysUserRelationOrg)表服务实现类
 *
 * @author yunze
 * @since 2024-11-17 20:26:16
 */
@Service
public class SysUserRelationOrgServiceImpl extends ServiceImpl<SysUserRelationOrgMapper, SysUserRelationOrg> implements SysUserRelationOrgService {

    @Override
    public Long save(SysUserRelationOrgAddDto dto) {
        SysUserRelationOrg bo = new SysUserRelationOrg();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysUserRelationOrgUpdateDto dto) {
        SysUserRelationOrg bo = new SysUserRelationOrg();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysUserRelationOrg> page(PageFilter<SysUserRelationOrgQueryDto> filter) {
        LambdaQueryWrapper<SysUserRelationOrg> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @DS("slave")
    @Override
    public List<Long> getUserIdByOrgId(Long orgId) {
        LambdaQueryWrapper<SysUserRelationOrg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysUserRelationOrg::getUserId);
        queryWrapper.eq(SysUserRelationOrg::getOrgId, orgId);
        List<SysUserRelationOrg> array = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(array)) {
            return Collections.emptyList();
        }
        return array.stream().map(SysUserRelationOrg::getUserId).collect(Collectors.toList());
    }
}

