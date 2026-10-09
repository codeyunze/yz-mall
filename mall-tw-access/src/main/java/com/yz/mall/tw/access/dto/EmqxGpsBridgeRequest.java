package com.yz.mall.tw.access.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * EMQX Rule HTTP 动作投递的上行 GPS（兼容多种包装）
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmqxGpsBridgeRequest {

    /**
     * MQTT Topic，如 tsp/TESTVIN001/up/gps
     */
    private String topic;
    /**
     * 设备 clientId
     */
    private String clientid;
    private String username;
    /**
     * 已是 GPS JSON 对象时直接用
     */
    private JsonNode payload;
    /**
     * payload 为字符串时
     */
    private String payload_str;
    /**
     * 部分规则把整段 GPS 展平到根节点
     */
    private String vin;
    private Object lng;
    private Object lat;
}
