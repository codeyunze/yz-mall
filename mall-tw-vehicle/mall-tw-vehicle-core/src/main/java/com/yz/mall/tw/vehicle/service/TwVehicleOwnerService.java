package com.yz.mall.tw.vehicle.service;

import com.yz.mall.tw.vehicle.dto.TwVehicleOwnerBindDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleOwnerTransferDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleOwnerUnbindDto;
import com.yz.mall.tw.vehicle.entity.TwVehicleOwner;

/**
 * 车主绑定服务
 */
public interface TwVehicleOwnerService {

    /**
     * 绑定车主
     *
     * @param dto 入参
     * @return 绑定记录ID
     */
    Long bind(TwVehicleOwnerBindDto dto);

    /**
     * 解绑车主（并撤销全部有效授权）
     *
     * @param dto 入参
     * @return 是否成功
     */
    Boolean unbind(TwVehicleOwnerUnbindDto dto);

    /**
     * 过户
     *
     * @param dto 入参
     * @return 新绑定记录ID
     */
    Long transfer(TwVehicleOwnerTransferDto dto);

    /**
     * 查询当前有效车主
     *
     * @param vehicleId 车辆ID
     * @return 绑定记录，可空
     */
    TwVehicleOwner getActiveOwner(Long vehicleId);

    /**
     * 车主名下有效车辆 ID 集合
     *
     * @param ownerUserId 车主用户ID
     * @return 车辆ID集合
     */
    java.util.Set<Long> listActiveVehicleIdsByOwner(Long ownerUserId);

    /**
     * 批量查询当前有效车主
     *
     * @param vehicleIds 车辆ID列表
     * @return vehicleId -> owner
     */
    java.util.Map<Long, TwVehicleOwner> listActiveByVehicleIds(java.util.List<Long> vehicleIds);
}
