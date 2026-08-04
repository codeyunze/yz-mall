package com.yz.mall.tw.device.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 重置凭证返回（含一次性新明文密码）
 */
@Data
public class TwDeviceCredResetVo {
    private String deviceId;
    private String mqttPassword;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime resetTime;
    private String hint = "请立即保存新密钥，旧连接将被踢下线";
}
