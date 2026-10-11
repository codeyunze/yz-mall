package com.yz.mall.tw.access.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 下行 MQTT 发布（供 tw-command 等调用）
 */
@Data
public class MqttPublishDto {

    /**
     * 完整 Topic；空则用 vin + 后缀拼
     */
    private String topic;
    /**
     * 车架号（拼默认 down/cmd 时用）
     */
    private String vin;
    /**
     * 默认后缀 down/cmd
     */
    private String suffix = "down/cmd";
    /**
     * 消息体
     */
    @NotBlank(message = "payload不能为空")
    private String payload;
    /**
     * QoS 0/1/2
     */
    private int qos = 1;
}
