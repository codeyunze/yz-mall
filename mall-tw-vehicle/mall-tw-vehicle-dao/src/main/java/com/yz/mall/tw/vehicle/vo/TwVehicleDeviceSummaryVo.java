package com.yz.mall.tw.vehicle.vo;

import lombok.Data;

/**
 * 绑定终端摘要（P0 可为空，待 tw-device）
 */
@Data
public class TwVehicleDeviceSummaryVo {

    private String deviceId;
    private Integer status;
}
