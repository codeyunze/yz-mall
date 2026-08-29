package com.yz.mall.tw.vehicle.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 启用/停用
 */
@Data
public class TwVehicleStatusDto {

    /**
     * 车辆ID
     */
    @NotNull(message = "车辆ID不能为空")
    private Long id;
    /**
     * 0停用 1启用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
