package com.yz.mall.tw.device.dto;

import lombok.Data;

/**
 * 注册终端
 */
@Data
public class TwDeviceAddDto {
    /**
     * 不传则服务端生成
     */
    private String deviceId;
    private String deviceName;
    /**
     * 默认 SIMULATOR
     */
    private String deviceType;
    private String remark;
    private Integer status;
}
