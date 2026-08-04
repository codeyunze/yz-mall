package com.yz.mall.tw.vehicle.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 撤销授权
 */
@Data
public class TwVehicleAuthRevokeDto {

    /**
     * 车辆ID
     */
    @NotNull(message = "车辆ID不能为空")
    private Long vehicleId;
    /**
     * 被授权用户ID（与 authId 二选一）
     */
    private Long authUserId;
    /**
     * 授权记录ID（优先）
     */
    private Long authId;
    /**
     * 撤销原因
     */
    private String remark;
}
