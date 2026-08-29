package com.yz.mall.tw.device.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 校验明文密码（跨服务）
 */
@Data
public class TwDeviceVerifyDto {

    @NotBlank(message = "deviceId不能为空")
    private String deviceId;

    @NotBlank(message = "密码不能为空")
    private String password;
}
