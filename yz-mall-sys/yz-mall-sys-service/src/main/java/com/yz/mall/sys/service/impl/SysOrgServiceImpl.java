package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.SysOrgAddDto;
import com.yz.mall.sys.dto.SysOrgQueryDto;
import com.yz.mall.sys.dto.SysOrgUpdateDto;
import com.yz.mall.sys.mapper.SysOrgMapper;
import com.yz.mall.sys.entity.SysOrg;
import com.yz.mall.sys.service.SysOrgService;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 系统-组织表(SysOrg)表服务实现类
 *
 * @author yunze
 * @since 2024-11-17 20:19:07
 */
@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements SysOrgService {

    @Override
    public Long save(SysOrgAddDto dto) {
        SysOrg bo = new SysOrg();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysOrgUpdateDto dto) {
        SysOrg bo = new SysOrg();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysOrg> page(PageFilter<SysOrgQueryDto> filter) {
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

