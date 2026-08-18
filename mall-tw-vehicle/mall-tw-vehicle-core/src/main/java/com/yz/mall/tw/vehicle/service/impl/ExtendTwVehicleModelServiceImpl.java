package com.yz.mall.tw.vehicle.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yz.mall.tw.vehicle.constant.TwVehicleConstants;
import com.yz.mall.tw.vehicle.entity.TwVehicleModel;
import com.yz.mall.tw.vehicle.entity.TwVehicleSeries;
import com.yz.mall.tw.vehicle.service.ExtendTwVehicleModelService;
import com.yz.mall.tw.vehicle.service.TwVehicleModelService;
import com.yz.mall.tw.vehicle.service.TwVehicleSeriesService;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleModelSlimVo;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 车型跨服务扩展 — 本域实现
 */
@Service
public class ExtendTwVehicleModelServiceImpl implements ExtendTwVehicleModelService {

    private final TwVehicleModelService modelService;
    private final TwVehicleSeriesService seriesService;

    public ExtendTwVehicleModelServiceImpl(TwVehicleModelService modelService, TwVehicleSeriesService seriesService) {
        this.modelService = modelService;
        this.seriesService = seriesService;
    }

    @Override
    public ExtendTwVehicleModelSlimVo getByCode(String modelCode) {
        if (StrUtil.isBlank(modelCode)) {
            return null;
        }
        TwVehicleModel model = modelService.getOne(new LambdaQueryWrapper<TwVehicleModel>()
                .eq(TwVehicleModel::getModelCode, modelCode.trim().toUpperCase())
                .last("limit 1"), false);
        if (model == null) {
            return null;
        }
        ExtendTwVehicleModelSlimVo vo = new ExtendTwVehicleModelSlimVo();
        vo.setModelCode(model.getModelCode());
        vo.setModelName(model.getModelName());
        vo.setSeriesCode(model.getSeriesCode());
        vo.setStatus(model.getStatus());
        TwVehicleSeries series = seriesService.getById(model.getSeriesId());
        if (series != null) {
            vo.setSeriesName(series.getSeriesName());
        }
        return vo;
    }

    @Override
    public Boolean existsEnabled(String modelCode) {
        if (StrUtil.isBlank(modelCode)) {
            return false;
        }
        TwVehicleModel model = modelService.getOne(new LambdaQueryWrapper<TwVehicleModel>()
                .eq(TwVehicleModel::getModelCode, modelCode.trim().toUpperCase())
                .last("limit 1"), false);
        return model != null && Objects.equals(model.getStatus(), TwVehicleConstants.STATUS_ENABLED);
    }
}
