package com.yz.mall.tw.vehicle.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 过户
 */
@Data
public class TwVehicleOwnerTransferDto {

    /**
     * 车辆ID
     */
    @NotNull(message = "车辆ID不能为空")
    private Long vehicleId;
    /**
     * 新车主用户ID
     */
    @NotNull(message = "新车主用户ID不能为空")
    private Long newOwnerUserId;
    /**
     * 过户说明
     */
    private String remark;
}
