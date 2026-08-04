package com.yz.mall.tw.device.dto;

import lombok.Data;

/**
 * 终端分页查询
 */
@Data
public class TwDeviceQueryDto {
    private String deviceId;
    private Integer status;
    private String vin;
    private Long vehicleId;
    private Integer onlineStatus;
    private String deviceType;
}
