package com.yz.mall.tw.device.dto;

import lombok.Data;

/**
 * 重置凭证：id 优先于 deviceId
 */
@Data
public class TwDeviceCredResetDto {
    private Long id;
    private String deviceId;
}
