package com.yz.mall.tw.access.feign;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 调 mall-tw 校验终端密码（字段与 mall-tw {@code TwDeviceVerifyDto} 对齐）
 */
@Data
public class TwDeviceVerifyRequest {

    @NotBlank
    private String deviceId;

    @NotBlank
    private String password;
}
