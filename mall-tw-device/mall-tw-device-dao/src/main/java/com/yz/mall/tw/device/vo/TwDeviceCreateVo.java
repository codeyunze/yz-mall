package com.yz.mall.tw.device.vo;

import lombok.Data;

/**
 * 注册成功返回（含一次性明文密码）
 */
@Data
public class TwDeviceCreateVo {
    private Long id;
    private String deviceId;
    private String mqttUsername;
    private String mqttPassword;
    private String hint = "请立即保存密钥，系统不再明文展示";
}
