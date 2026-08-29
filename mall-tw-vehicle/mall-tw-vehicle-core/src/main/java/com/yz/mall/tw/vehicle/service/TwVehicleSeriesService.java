package com.yz.mall.tw.vehicle.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.tw.vehicle.dto.TwVehicleMasterStatusDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleSeriesAddDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleSeriesQueryDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleSeriesUpdateDto;
import com.yz.mall.tw.vehicle.entity.TwVehicleSeries;
import com.yz.mall.tw.vehicle.vo.TwVehicleSeriesOptionVo;
import com.yz.mall.tw.vehicle.vo.TwVehicleSeriesVo;

import java.util.List;

/**
 * 车系服务
 */
public interface TwVehicleSeriesService extends IService<TwVehicleSeries> {

    /**
     * 新建车系
     */
    Long add(TwVehicleSeriesAddDto dto);

    /**
     * 编辑车系
     */
    Boolean edit(TwVehicleSeriesUpdateDto dto);

    /**
     * 启停
     */
    Boolean changeStatus(TwVehicleMasterStatusDto dto);

    /**
     * 逻辑删除（下属有车型则拒绝）
     */
    Boolean deleteSeries(Long id);

    /**
     * 分页
     */
    Page<TwVehicleSeriesVo> pageSeries(PageFilter<TwVehicleSeriesQueryDto> filter);

    /**
     * 详情
     */
    TwVehicleSeriesVo detail(Long id);

    /**
     * 启用车系下拉
     */
    List<TwVehicleSeriesOptionVo> options();

    /**
     * 获取启用中的车系，不存在则抛错
     */
    TwVehicleSeries requireEnabledSeries(Long seriesId, String seriesCode);
}
