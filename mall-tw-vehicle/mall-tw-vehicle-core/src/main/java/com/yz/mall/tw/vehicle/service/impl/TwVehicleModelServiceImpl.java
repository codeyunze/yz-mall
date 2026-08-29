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
import com.yz.mall.tw.vehicle.dto.TwVehicleModelAddDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleModelQueryDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleModelUpdateDto;
import com.yz.mall.tw.vehicle.entity.TwVehicle;
import com.yz.mall.tw.vehicle.entity.TwVehicleModel;
import com.yz.mall.tw.vehicle.entity.TwVehicleSeries;
import com.yz.mall.tw.vehicle.mapper.TwVehicleMapper;
import com.yz.mall.tw.vehicle.mapper.TwVehicleModelMapper;
import com.yz.mall.tw.vehicle.service.TwVehicleModelService;
import com.yz.mall.tw.vehicle.service.TwVehicleSeriesService;
import com.yz.mall.tw.vehicle.vo.TwVehicleModelOptionVo;
import com.yz.mall.tw.vehicle.vo.TwVehicleModelVo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 车型服务实现
 */
@Service
public class TwVehicleModelServiceImpl extends ServiceImpl<TwVehicleModelMapper, TwVehicleModel> implements TwVehicleModelService {

    private final TwVehicleSeriesService seriesService;
    private final TwVehicleMapper vehicleMapper;

    public TwVehicleModelServiceImpl(@Lazy TwVehicleSeriesService seriesService, TwVehicleMapper vehicleMapper) {
        this.seriesService = seriesService;
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(TwVehicleModelAddDto dto) {
        TwVehicleSeries series = seriesService.requireEnabledSeries(dto.getSeriesId(), dto.getSeriesCode());
        String modelCode = normalizeCode(dto.getModelCode());
        if (count(new LambdaQueryWrapper<TwVehicleModel>().eq(TwVehicleModel::getModelCode, modelCode)) > 0) {
            throw new BusinessException("编码已存在");
        }
        TwVehicleModel entity = new TwVehicleModel();
        BeanUtil.copyProperties(dto, entity);
        entity.setId(IdUtil.getSnowflakeNextId());
        entity.setSeriesId(series.getId());
        entity.setSeriesCode(series.getSeriesCode());
        entity.setModelCode(modelCode);
        entity.setSortNo(dto.getSortNo() == null ? 0 : dto.getSortNo());
        entity.setStatus(dto.getStatus() == null ? TwVehicleConstants.STATUS_ENABLED : dto.getStatus());
        entity.setCreateId(StpUtil.getLoginIdAsLong());
        entity.setUpdateId(entity.getCreateId());
        save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean edit(TwVehicleModelUpdateDto dto) {
        TwVehicleModel entity = requireModel(dto.getId());
        if (dto.getModelName() != null) {
            entity.setModelName(dto.getModelName());
        }
        if (dto.getEnergyType() != null) {
            entity.setEnergyType(dto.getEnergyType());
        }
        if (dto.getDriveType() != null) {
            entity.setDriveType(dto.getDriveType());
        }
        if (dto.getSeatCount() != null) {
            entity.setSeatCount(dto.getSeatCount());
        }
        if (dto.getBatteryKwh() != null) {
            entity.setBatteryKwh(dto.getBatteryKwh());
        }
        if (dto.getRangeKm() != null) {
            entity.setRangeKm(dto.getRangeKm());
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
        if (!Objects.equals(dto.getStatus(), TwVehicleConstants.STATUS_ENABLED) && !Objects.equals(dto.getStatus(), TwVehicleConstants.STATUS_DISABLED)) {
            throw new BusinessException("状态值非法");
        }
        TwVehicleModel entity = requireModel(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setUpdateId(StpUtil.getLoginIdAsLong());
        return updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteModel(Long id) {
        TwVehicleModel entity = requireModel(id);
        long ref = vehicleMapper.selectCount(new LambdaQueryWrapper<TwVehicle>().eq(TwVehicle::getModelCode, entity.getModelCode()));
        if (ref > 0) {
            throw new BusinessException("车型已被车辆引用，无法删除");
        }
        return lambdaUpdate().set(TwVehicleModel::getInvalid, id).set(TwVehicleModel::getUpdateId, StpUtil.getLoginIdAsLong())
                .eq(TwVehicleModel::getId, id).eq(TwVehicleModel::getInvalid, 0L).update();
    }

    @Override
    public Page<TwVehicleModelVo> pageModels(PageFilter<TwVehicleModelQueryDto> filter) {
        TwVehicleModelQueryDto query = filter.getFilter() == null ? new TwVehicleModelQueryDto() : filter.getFilter();
        LambdaQueryWrapper<TwVehicleModel> wrapper = new LambdaQueryWrapper<>();
        if (query.getSeriesId() != null) {
            wrapper.eq(TwVehicleModel::getSeriesId, query.getSeriesId());
        }
        if (StrUtil.isNotBlank(query.getSeriesCode())) {
            wrapper.eq(TwVehicleModel::getSeriesCode, normalizeCode(query.getSeriesCode()));
        }
        if (StrUtil.isNotBlank(query.getModelCode())) {
            wrapper.likeRight(TwVehicleModel::getModelCode, normalizeCode(query.getModelCode()));
        }
        if (StrUtil.isNotBlank(query.getModelName())) {
            wrapper.like(TwVehicleModel::getModelName, query.getModelName().trim());
        }
        if (query.getStatus() != null) {
            wrapper.eq(TwVehicleModel::getStatus, query.getStatus());
        }
        if (query.getEnergyType() != null) {
            wrapper.eq(TwVehicleModel::getEnergyType, query.getEnergyType());
        }
        wrapper.orderByAsc(TwVehicleModel::getSortNo).orderByDesc(TwVehicleModel::getCreateTime);
        Page<TwVehicleModel> page = page(new Page<>(filter.getCurrent(), filter.getSize()), wrapper);
        Map<Long, String> seriesNameMap = loadSeriesNames(page.getRecords().stream().map(TwVehicleModel::getSeriesId).collect(Collectors.toSet()));
        List<TwVehicleModelVo> vos = new ArrayList<>();
        for (TwVehicleModel item : page.getRecords()) {
            TwVehicleModelVo vo = toVo(item);
            vo.setSeriesName(seriesNameMap.get(item.getSeriesId()));
            vos.add(vo);
        }
        Page<TwVehicleModelVo> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(vos);
        return result;
    }

    @Override
    public TwVehicleModelVo detail(Long id) {
        TwVehicleModel entity = requireModel(id);
        TwVehicleModelVo vo = toVo(entity);
        TwVehicleSeries series = seriesService.getById(entity.getSeriesId());
        if (series != null) {
            vo.setSeriesName(series.getSeriesName());
        }
        return vo;
    }

    @Override
    public List<TwVehicleModelOptionVo> options(Long seriesId, String seriesCode) {
        TwVehicleSeries series = seriesService.requireEnabledSeries(seriesId, seriesCode);
        List<TwVehicleModel> list = list(new LambdaQueryWrapper<TwVehicleModel>()
                .eq(TwVehicleModel::getSeriesId, series.getId())
                .eq(TwVehicleModel::getStatus, TwVehicleConstants.STATUS_ENABLED)
                .orderByAsc(TwVehicleModel::getSortNo)
                .orderByAsc(TwVehicleModel::getId));
        List<TwVehicleModelOptionVo> result = new ArrayList<>();
        for (TwVehicleModel item : list) {
            TwVehicleModelOptionVo vo = new TwVehicleModelOptionVo();
            vo.setId(item.getId());
            vo.setModelCode(item.getModelCode());
            vo.setModelName(item.getModelName());
            vo.setEnergyType(item.getEnergyType());
            vo.setRangeKm(item.getRangeKm());
            result.add(vo);
        }
        return result;
    }

    @Override
    public TwVehicleModel requireEnabledByCode(String modelCode) {
        if (StrUtil.isBlank(modelCode)) {
            throw new BusinessException("请选择有效车型");
        }
        TwVehicleModel model = getOne(new LambdaQueryWrapper<TwVehicleModel>()
                .eq(TwVehicleModel::getModelCode, normalizeCode(modelCode))
                .last("limit 1"), false);
        if (model == null || !Objects.equals(model.getStatus(), TwVehicleConstants.STATUS_ENABLED)) {
            throw new BusinessException("请选择有效车型");
        }
        return model;
    }

    @Override
    public int countBySeriesId(Long seriesId) {
        return (int) count(new LambdaQueryWrapper<TwVehicleModel>().eq(TwVehicleModel::getSeriesId, seriesId));
    }

    private TwVehicleModel requireModel(Long id) {
        TwVehicleModel entity = getById(id);
        if (entity == null) {
            throw new BusinessException("车型不存在");
        }
        return entity;
    }

    private Map<Long, String> loadSeriesNames(Set<Long> seriesIds) {
        if (seriesIds == null || seriesIds.isEmpty()) {
            return Map.of();
        }
        return seriesService.listByIds(seriesIds).stream()
                .collect(Collectors.toMap(TwVehicleSeries::getId, TwVehicleSeries::getSeriesName, (a, b) -> a));
    }

    private static TwVehicleModelVo toVo(TwVehicleModel entity) {
        TwVehicleModelVo vo = new TwVehicleModelVo();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }

    private static String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }
}
