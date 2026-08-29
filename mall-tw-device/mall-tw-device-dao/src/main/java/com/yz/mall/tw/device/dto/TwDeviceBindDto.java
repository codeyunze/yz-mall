package com.yz.mall.tw.device.dto;

import lombok.Data;

/**
 * 绑定车辆：id 优先于 deviceId；vehicleId 优先于 vin
 */
@Data
public class TwDeviceBindDto {
    private Long id;
    private String deviceId;
    private Long vehicleId;
    private String vin;
    private String remark;
}
