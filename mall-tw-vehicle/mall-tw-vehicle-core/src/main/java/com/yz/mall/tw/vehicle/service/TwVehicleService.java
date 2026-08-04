package com.yz.mall.tw.vehicle.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.tw.vehicle.dto.TwVehicleAddDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleQueryDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleStatusDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleUpdateDto;
import com.yz.mall.tw.vehicle.entity.TwVehicle;
import com.yz.mall.tw.vehicle.vo.TwVehicleDetailVo;
import com.yz.mall.tw.vehicle.vo.TwVehiclePageVo;

/**
 * 车辆档案服务
 */
public interface TwVehicleService extends IService<TwVehicle> {

    /**
     * 新建车辆
     *
     * @param dto 入参
     * @return 车辆ID
     */
    Long add(TwVehicleAddDto dto);

    /**
     * 编辑车辆（不可改 VIN）
     *
     * @param dto 入参
     * @return 是否成功
     */
    Boolean edit(TwVehicleUpdateDto dto);

    /**
     * 启用/停用
     *
     * @param dto 入参
     * @return 是否成功
     */
    Boolean changeStatus(TwVehicleStatusDto dto);

    /**
     * 逻辑删除（invalid=id）；有有效车主或授权时拒绝
     *
     * @param id 车辆ID
     * @return 是否成功
     */
    Boolean deleteVehicle(Long id);

    /**
     * 分页查询
     *
     * @param filter 分页过滤
     * @return 分页结果
     */
    Page<TwVehiclePageVo> pageVehicles(PageFilter<TwVehicleQueryDto> filter);

    /**
     * 详情
     *
     * @param id 车辆ID
     * @return 详情
     */
    TwVehicleDetailVo detail(Long id);

    /**
     * 获取有效车辆，不存在则抛业务异常
     *
     * @param id 车辆ID
     * @return 实体
     */
    TwVehicle requireVehicle(Long id);
}
