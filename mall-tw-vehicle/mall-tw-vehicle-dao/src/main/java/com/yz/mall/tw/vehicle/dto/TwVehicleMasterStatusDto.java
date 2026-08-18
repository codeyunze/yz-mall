package com.yz.mall.tw.vehicle.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 车系/车型启停
 */
@Data
public class TwVehicleMasterStatusDto {

    /**
     * 主键ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;
    /**
     * 0停用 1启用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
