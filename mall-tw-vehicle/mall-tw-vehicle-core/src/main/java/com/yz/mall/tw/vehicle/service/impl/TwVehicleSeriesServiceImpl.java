package com.yz.mall.tw.vehicle.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.tw.vehicle.constant.TwVehicleConstants;
import com.yz.mall.tw.vehicle.dto.TwVehicleMasterStatusDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleSeriesAddDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleSeriesQueryDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleSeriesUpdateDto;
import com.yz.mall.tw.vehicle.entity.TwVehicleSeries;
import com.yz.mall.tw.vehicle.mapper.TwVehicleSeriesMapper;
import com.yz.mall.tw.vehicle.service.TwVehicleModelService;
import com.yz.mall.tw.vehicle.service.TwVehicleSeriesService;
import com.yz.mall.tw.vehicle.vo.TwVehicleSeriesOptionVo;
import com.yz.mall.tw.vehicle.vo.TwVehicleSeriesVo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 车系服务实现
 */
@Service
public class TwVehicleSeriesServiceImpl extends ServiceImpl<TwVehicleSeriesMapper, TwVehicleSeries> implements TwVehicleSeriesService {

    private final TwVehicleModelService modelService;

    public TwVehicleSeriesServiceImpl(@Lazy TwVehicleModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(TwVehicleSeriesAddDto dto) {
        String code = normalizeCode(dto.getSeriesCode());
        if (count(new LambdaQueryWrapper<TwVehicleSeries>().eq(TwVehicleSeries::getSeriesCode, code)) > 0) {
            throw new BusinessException("编码已存在");
        }
        TwVehicleSeries entity = new TwVehicleSeries();
        BeanUtil.copyProperties(dto, entity);
        entity.setId(IdUtil.getSnowflakeNextId());
        entity.setSeriesCode(code);
        entity.setSortNo(dto.getSortNo() == null ? 0 : dto.getSortNo());
        entity.setStatus(dto.getStatus() == null ? TwVehicleConstants.STATUS_ENABLED : dto.getStatus());
        entity.setCreateId(StpUtil.getLoginIdAsLong());
        entity.setUpdateId(entity.getCreateId());
        save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean edit(TwVehicleSeriesUpdateDto dto) {
        TwVehicleSeries entity = requireSeries(dto.getId());
        if (dto.getSeriesName() != null) {
            entity.setSeriesName(dto.getSeriesName());
        }
        if (dto.getBrandName() != null) {
            entity.setBrandName(dto.getBrandName());
        }
        if (dto.getCoverFileId() != null) {
            entity.setCoverFileId(dto.getCoverFileId());
        }
        if (dto.getSortNo() != null) {
            entity.setSortNo(dto.getSortNo());
        }
        if (dto.getRemark() != null) {
            entity.setRemark(dto.getRemark());
        }
        entity.setUpdateId(StpUtil.getLoginIdAsLong());
        return updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeStatus(TwVehicleMasterStatusDto dto) {
        assertStatus(dto.getStatus());
        TwVehicleSeries entity = requireSeries(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setUpdateId(StpUtil.getLoginIdAsLong());
        return updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSeries(Long id) {
        requireSeries(id);
        if (modelService.countBySeriesId(id) > 0) {
            throw new BusinessException("请先删除或迁移下属车型");
        }
        return lambdaUpdate().set(TwVehicleSeries::getInvalid, id).set(TwVehicleSeries::getUpdateId, StpUtil.getLoginIdAsLong())
                .eq(TwVehicleSeries::getId, id).eq(TwVehicleSeries::getInvalid, 0L).update();
    }

    @Override
    public Page<TwVehicleSeriesVo> pageSeries(PageFilter<TwVehicleSeriesQueryDto> filter) {
        TwVehicleSeriesQueryDto query = filter.getFilter() == null ? new TwVehicleSeriesQueryDto() : filter.getFilter();
        LambdaQueryWrapper<TwVehicleSeries> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getSeriesCode())) {
            wrapper.likeRight(TwVehicleSeries::getSeriesCode, normalizeCode(query.getSeriesCode()));
        }
        if (StrUtil.isNotBlank(query.getSeriesName())) {
            wrapper.like(TwVehicleSeries::getSeriesName, query.getSeriesName().trim());
        }
        if (StrUtil.isNotBlank(query.getBrandName())) {
            wrapper.like(TwVehicleSeries::getBrandName, query.getBrandName().trim());
        }
        if (query.getStatus() != null) {
            wrapper.eq(TwVehicleSeries::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(TwVehicleSeries::getSortNo).orderByDesc(TwVehicleSeries::getCreateTime);
        Page<TwVehicleSeries> page = page(new Page<>(filter.getCurrent(), filter.getSize()), wrapper);
        List<TwVehicleSeriesVo> vos = new ArrayList<>();
        for (TwVehicleSeries item : page.getRecords()) {
            TwVehicleSeriesVo vo = toVo(item);
            vo.setModelCount(modelService.countBySeriesId(item.getId()));
            vos.add(vo);
        }
        Page<TwVehicleSeriesVo> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(vos);
        return result;
    }

    @Override
    public TwVehicleSeriesVo detail(Long id) {
        TwVehicleSeriesVo vo = toVo(requireSeries(id));
        vo.setModelCount(modelService.countBySeriesId(id));
        return vo;
    }

    @Override
    public List<TwVehicleSeriesOptionVo> options() {
        List<TwVehicleSeries> list = list(new LambdaQueryWrapper<TwVehicleSeries>()
                .eq(TwVehicleSeries::getStatus, TwVehicleConstants.STATUS_ENABLED)
                .orderByAsc(TwVehicleSeries::getSortNo)
                .orderByAsc(TwVehicleSeries::getId));
        List<TwVehicleSeriesOptionVo> result = new ArrayList<>();
        for (TwVehicleSeries item : list) {
            TwVehicleSeriesOptionVo vo = new TwVehicleSeriesOptionVo();
            vo.setId(item.getId());
            vo.setSeriesCode(item.getSeriesCode());
            vo.setSeriesName(item.getSeriesName());
            result.add(vo);
        }
        return result;
    }

    @Override
    public TwVehicleSeries requireEnabledSeries(Long seriesId, String seriesCode) {
        TwVehicleSeries series = null;
        if (seriesId != null) {
            series = getById(seriesId);
        } else if (StrUtil.isNotBlank(seriesCode)) {
            series = getOne(new LambdaQueryWrapper<TwVehicleSeries>().eq(TwVehicleSeries::getSeriesCode, normalizeCode(seriesCode)).last("limit 1"), false);
        }
        if (series == null || !Objects.equals(series.getStatus(), TwVehicleConstants.STATUS_ENABLED)) {
            throw new BusinessException("车系不可用");
        }
        return series;
    }

    private TwVehicleSeries requireSeries(Long id) {
        TwVehicleSeries entity = getById(id);
        if (entity == null) {
            throw new BusinessException("车系不存在");
        }
        return entity;
    }

    private static TwVehicleSeriesVo toVo(TwVehicleSeries entity) {
        TwVehicleSeriesVo vo = new TwVehicleSeriesVo();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }

    private static String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private static void assertStatus(Integer status) {
        if (!Objects.equals(status, TwVehicleConstants.STATUS_ENABLED) && !Objects.equals(status, TwVehicleConstants.STATUS_DISABLED)) {
            throw new BusinessException("状态值非法");
        }
    }
}
