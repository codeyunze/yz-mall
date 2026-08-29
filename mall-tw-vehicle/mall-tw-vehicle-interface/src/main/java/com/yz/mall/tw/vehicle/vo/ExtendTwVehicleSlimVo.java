package com.yz.mall.tw.vehicle.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 跨服务车辆摘要
 */
@Data
public class ExtendTwVehicleSlimVo implements Serializable {

    private Long id;
    private String vin;
    private String plateNo;
    private Integer status;
    private Long invalid;
}
