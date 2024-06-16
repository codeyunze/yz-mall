package com.yz.cases.mall.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.cases.mall.dto.SysDatasourceAddDto;
import com.yz.cases.mall.dto.SysDatasourceQueryDto;
import com.yz.cases.mall.dto.SysDatasourceUpdateDto;
import com.yz.cases.mall.mapper.SysDatasourceMapper;
import com.yz.cases.mall.entity.SysDatasource;
import com.yz.cases.mall.service.SysDatasourceService;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 系统-数据源信息(SysDatasource)表服务实现类
 *
 * @author yunze
 * @since 2024-06-12 11:02:09
 */
@Service
public class SysDatasourceServiceImpl extends ServiceImpl<SysDatasourceMapper, SysDatasource> implements SysDatasourceService {

    @Override
    public Long save(SysDatasourceAddDto dto) {
        SysDatasource bo = new SysDatasource();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysDatasourceUpdateDto dto) {
        SysDatasource bo = new SysDatasource();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysDatasource> page(PageFilter<SysDatasourceQueryDto> filter) {
        LambdaQueryWrapper<SysDatasource> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

