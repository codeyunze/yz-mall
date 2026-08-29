package com.yz.mall.tw.vehicle.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 绑定车主
 */
@Data
public class TwVehicleOwnerBindDto {

    /**
     * 车辆ID
     */
    @NotNull(message = "车辆ID不能为空")
    private Long vehicleId;
    /**
     * 车主用户ID
     */
    @NotNull(message = "车主用户ID不能为空")
    private Long ownerUserId;
    /**
     * 备注
     */
    private String remark;
}
