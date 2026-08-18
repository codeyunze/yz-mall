package com.yz.mall.tw.vehicle.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 跨服务车型摘要
 */
@Data
public class ExtendTwVehicleModelSlimVo implements Serializable {

    private String modelCode;
    private String modelName;
    private String seriesCode;
    private String seriesName;
    private Integer status;
}
