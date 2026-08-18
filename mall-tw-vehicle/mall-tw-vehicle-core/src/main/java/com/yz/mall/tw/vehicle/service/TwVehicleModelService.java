package com.yz.mall.tw.vehicle.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.tw.vehicle.dto.TwVehicleMasterStatusDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleModelAddDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleModelQueryDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleModelUpdateDto;
import com.yz.mall.tw.vehicle.entity.TwVehicleModel;
import com.yz.mall.tw.vehicle.vo.TwVehicleModelOptionVo;
import com.yz.mall.tw.vehicle.vo.TwVehicleModelVo;

import java.util.List;

/**
 * 车型服务
 */
public interface TwVehicleModelService extends IService<TwVehicleModel> {

    /**
     * 新建车型
     */
    Long add(TwVehicleModelAddDto dto);

    /**
     * 编辑车型
     */
    Boolean edit(TwVehicleModelUpdateDto dto);

    /**
     * 启停
     */
    Boolean changeStatus(TwVehicleMasterStatusDto dto);

    /**
     * 逻辑删除（被车辆引用则拒绝）
     */
    Boolean deleteModel(Long id);

    /**
     * 分页
     */
    Page<TwVehicleModelVo> pageModels(PageFilter<TwVehicleModelQueryDto> filter);

    /**
     * 详情
     */
    TwVehicleModelVo detail(Long id);

    /**
     * 按车系启用车型下拉
     */
    List<TwVehicleModelOptionVo> options(Long seriesId, String seriesCode);

    /**
     * 按编码取启用中的车型，不存在则抛业务异常
     */
    TwVehicleModel requireEnabledByCode(String modelCode);

    /**
     * 统计车系下有效车型数
     */
    int countBySeriesId(Long seriesId);
}
