package com.yz.mall.tw.device.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 编辑终端（不可改 deviceId）
 */
@Data
public class TwDeviceUpdateDto {
    @NotNull(message = "终端ID不能为空")
    private Long id;
    private String deviceName;
    private String deviceType;
    private String remark;
    private String firmwareVersion;
}
