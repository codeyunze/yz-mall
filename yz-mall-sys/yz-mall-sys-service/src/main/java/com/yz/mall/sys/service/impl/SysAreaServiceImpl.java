package com.yz.mall.sys.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.SysAreaAddDto;
import com.yz.mall.sys.dto.SysAreaQueryDto;
import com.yz.mall.sys.dto.SysAreaUpdateDto;
import com.yz.mall.sys.entity.SysArea;
import com.yz.mall.sys.enums.EnableEnum;
import com.yz.mall.sys.mapper.SysAreaMapper;
import com.yz.mall.sys.service.SysAreaService;
import com.yz.mall.sys.vo.AreaVo;
import com.yz.mall.web.common.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * (SysArea)表服务实现类
 *
 * @author yunze
 * @since 2025-03-03 22:38:56
 */
@Service
public class SysAreaServiceImpl extends ServiceImpl<SysAreaMapper, SysArea> implements SysAreaService {

    @Override
    public String save(SysAreaAddDto dto) {
        SysArea bo = new SysArea();
        BeanUtils.copyProperties(dto, bo);
        baseMapper.insert(bo);
        return bo.getId();
    }

    // 清理缓存
    @Caching(evict = {
            @CacheEvict(value = "system:area-administer", key = "#dto.id"),
            @CacheEvict(value = "system:area-detail", key = "#dto.id")
    })
    @Override
    public boolean update(SysAreaUpdateDto dto) {
        SysArea bo = new SysArea();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysArea> page(PageFilter<SysAreaQueryDto> filter) {
        LambdaQueryWrapper<SysArea> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysArea::getStatus, EnableEnum.ENABLE.get());
        queryWrapper.eq(StringUtils.hasText(filter.getFilter().getParentId()), SysArea::getParentId, filter.getFilter().getParentId());
        queryWrapper.orderByAsc(SysArea::getId);
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    // 添加缓存
    @Cacheable(value = "system:area-administer", key = "#parentId")
    @DS("slave")
    @Override
    public List<AreaVo> getByParentId(String parentId) {
        LambdaQueryWrapper<SysArea> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysArea::getStatus, EnableEnum.ENABLE.get());
        queryWrapper.eq(SysArea::getParentId, parentId);
        queryWrapper.orderByAsc(SysArea::getSortNum);
        List<SysArea> sysAreas = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(sysAreas)) {
            return Collections.emptyList();
        }

        List<AreaVo> areas = new ArrayList<>();
        sysAreas.forEach(sysArea -> {
            AreaVo areaVo = new AreaVo(parentId, sysArea);
            areas.add(areaVo);
        });
        return areas;
    }
}

