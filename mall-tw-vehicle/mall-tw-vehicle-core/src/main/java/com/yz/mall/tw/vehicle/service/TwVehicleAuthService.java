package com.yz.mall.tw.vehicle.service;

import com.yz.mall.tw.vehicle.dto.TwVehicleAuthGrantDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleAuthRevokeDto;
import com.yz.mall.tw.vehicle.entity.TwVehicleAuth;
import com.yz.mall.tw.vehicle.vo.TwVehicleAuthVo;

import java.util.List;

/**
 * 车辆授权服务
 */
public interface TwVehicleAuthService {

    /**
     * 授权（同用户有效授权则更新 scope/过期）
     *
     * @param dto 入参
     * @return 授权记录ID
     */
    Long grant(TwVehicleAuthGrantDto dto);

    /**
     * 撤销授权
     *
     * @param dto 入参
     * @return 是否成功
     */
    Boolean revoke(TwVehicleAuthRevokeDto dto);

    /**
     * 授权列表
     *
     * @param vehicleId      车辆ID
     * @param includeHistory 是否含历史
     * @return 列表
     */
    List<TwVehicleAuthVo> listAuth(Long vehicleId, boolean includeHistory);

    /**
     * 撤销车辆全部有效授权（解绑/过户用）
     *
     * @param vehicleId 车辆ID
     * @param operatorId 操作人
     */
    void revokeAllActive(Long vehicleId, Long operatorId);

    /**
     * 当前用户对车辆的有效授权
     *
     * @param vehicleId 车辆ID
     * @param userId    用户ID
     * @return 授权，可空
     */
    TwVehicleAuth getActiveAuth(Long vehicleId, Long userId);

    /**
     * 有效授权人数
     *
     * @param vehicleId 车辆ID
     * @return 人数
     */
    int countActive(Long vehicleId);

    /**
     * 被授权用户可见车辆 ID
     *
     * @param authUserId 被授权用户ID
     * @return 车辆ID集合
     */
    java.util.Set<Long> listActiveVehicleIdsByAuthUser(Long authUserId);

    /**
     * 批量统计有效授权人数
     *
     * @param vehicleIds 车辆ID列表
     * @return vehicleId -> count
     */
    java.util.Map<Long, Integer> countActiveByVehicleIds(java.util.List<Long> vehicleIds);
}
