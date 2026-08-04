package com.yz.mall.tw.device.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 启用/禁用
 */
@Data
public class TwDeviceStatusDto {
    @NotNull(message = "终端ID不能为空")
    private Long id;
    @NotNull(message = "状态不能为空")
    private Integer status;
}
