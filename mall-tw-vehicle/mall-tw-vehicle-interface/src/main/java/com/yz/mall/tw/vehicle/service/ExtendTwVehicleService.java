package com.yz.mall.tw.vehicle.service;

import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleAccessVo;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleSlimVo;

/**
 * 车辆档案跨服务扩展接口
 */
public interface ExtendTwVehicleService {

    /**
     * 按 VIN 查有效车辆摘要
     *
     * @param vin 车架号
     * @return 摘要，不存在返回 null
     */
    ExtendTwVehicleSlimVo getByVin(String vin);

    /**
     * 按主键查有效车辆摘要
     *
     * @param id 车辆ID
     * @return 摘要，不存在返回 null
     */
    ExtendTwVehicleSlimVo getById(Long id);

    /**
     * 是否存在且启用
     *
     * @param id 车辆ID
     * @return true=存在且启用
     */
    Boolean existsEnabled(Long id);

    /**
     * 校验用户对车辆的访问能力
     *
     * @param vehicleId 车辆ID（与 vin 二选一优先）
     * @param vin       VIN
     * @param userId    用户ID
     * @param scope     所需能力位
     * @return 访问结果
     */
    ExtendTwVehicleAccessVo checkAccess(Long vehicleId, String vin, Long userId, Integer scope);
}
