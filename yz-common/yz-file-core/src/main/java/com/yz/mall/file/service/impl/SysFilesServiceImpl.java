package com.yz.mall.file.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.file.dto.SysFilesAddDto;
import com.yz.mall.file.dto.SysFilesQueryDto;
import com.yz.mall.file.dto.SysFilesUpdateDto;
import com.yz.mall.file.mapper.SysFilesMapper;
import com.yz.mall.file.entity.SysFiles;
import com.yz.mall.file.service.SysFilesService;
import com.yz.mall.web.common.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 系统-文件表(SysFiles)表服务实现类
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
@Service
public class SysFilesServiceImpl extends ServiceImpl<SysFilesMapper, SysFiles> implements SysFilesService {

    @Override
    public Long save(SysFilesAddDto dto) {
        SysFiles bo = new SysFiles();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysFilesUpdateDto dto) {
        SysFiles bo = new SysFiles();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysFiles> page(PageFilter<SysFilesQueryDto> filter) {
        LambdaQueryWrapper<SysFiles> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

