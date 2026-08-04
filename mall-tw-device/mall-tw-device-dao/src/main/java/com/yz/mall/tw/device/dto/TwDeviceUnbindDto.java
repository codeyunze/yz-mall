package com.yz.mall.tw.device.dto;

import lombok.Data;

/**
 * 解绑：能唯一定位当前有效绑定即可
 */
@Data
public class TwDeviceUnbindDto {
    private Long id;
    private String deviceId;
    private Long vehicleId;
    private String vin;
    private String remark;
}
