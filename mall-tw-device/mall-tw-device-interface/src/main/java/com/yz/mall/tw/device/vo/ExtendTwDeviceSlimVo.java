package com.yz.mall.tw.device.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 车辆详情聚合用终端摘要（无密钥）
 */
@Data
public class ExtendTwDeviceSlimVo implements Serializable {
    private Long id;
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private Integer status;
    private Long vehicleId;
    private String vin;
}
