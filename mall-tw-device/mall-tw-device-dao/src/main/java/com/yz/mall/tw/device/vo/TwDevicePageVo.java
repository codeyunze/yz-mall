package com.yz.mall.tw.device.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 终端分页行（无密钥）
 */
@Data
public class TwDevicePageVo {
    private Long id;
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private Integer status;
    private String vin;
    private Long vehicleId;
    private String plateNo;
    private Boolean online;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime lastOnlineTime;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;
}
