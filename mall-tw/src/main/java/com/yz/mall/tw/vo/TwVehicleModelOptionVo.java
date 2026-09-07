package com.yz.mall.tw.vo;

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
