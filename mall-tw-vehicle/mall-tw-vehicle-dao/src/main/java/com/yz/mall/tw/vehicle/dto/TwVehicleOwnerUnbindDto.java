package com.yz.mall.tw.vehicle.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 解绑车主
 */
@Data
public class TwVehicleOwnerUnbindDto {

    /**
     * 车辆ID
     */
    @NotNull(message = "车辆ID不能为空")
    private Long vehicleId;
    /**
     * 解绑原因
     */
    private String remark;
}
