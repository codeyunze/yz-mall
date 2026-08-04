package com.yz.mall.tw.device.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 终端详情（无密钥）
 */
@Data
public class TwDeviceDetailVo {
    private Long id;
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private Integer status;
    private String firmwareVersion;
    private String certSn;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime certExpireTime;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime lastCredResetTime;
    private String remark;
    private Long vehicleId;
    private String vin;
    private String plateNo;
    private Boolean online;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime bindTime;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;
}
