package com.yz.mall.au.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.au.dto.SysAccountAuAddDto;
import com.yz.mall.au.dto.SysAccountAuQueryDto;
import com.yz.mall.au.dto.SysAccountAuUpdateDto;
import com.yz.mall.au.mapper.SysAccountAuMapper;
import com.yz.mall.au.entity.SysAccountAu;
import com.yz.mall.au.service.SysAccountAuService;
import com.yz.mall.web.common.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 个人黄金账户(SysAccountAu)表服务实现类
 *
 * @author yunze
 * @since 2025-01-05 10:06:32
 */
@Service
public class SysAccountAuServiceImpl extends ServiceImpl<SysAccountAuMapper, SysAccountAu> implements SysAccountAuService {

    @Override
    public Long save(SysAccountAuAddDto dto) {
        SysAccountAu bo = new SysAccountAu();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setUserId(Long.parseLong(StpUtil.getLoginId().toString()));
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysAccountAuUpdateDto dto) {
        SysAccountAu bo = new SysAccountAu();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysAccountAu> page(PageFilter<SysAccountAuQueryDto> filter) {
        LambdaQueryWrapper<SysAccountAu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(filter.getFilter().getTransactionType() != null, SysAccountAu::getTransactionType, filter.getFilter().getTransactionType());
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

