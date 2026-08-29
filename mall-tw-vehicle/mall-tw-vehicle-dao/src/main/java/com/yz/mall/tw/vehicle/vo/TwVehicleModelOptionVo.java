package com.yz.mall.tw.vehicle.vo;

import lombok.Data;

/**
 * 车型下拉项
 */
@Data
public class TwVehicleModelOptionVo {

    private Long id;
    private String modelCode;
    private String modelName;
    private Integer energyType;
    private Integer rangeKm;
}
