package com.yz.mall.tw.access.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * EMQX Webhook 事件（连接/断开等）
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmqxWebhookEvent {

    /**
     * 如 client.connected / client.disconnected
     */
    private String event;
    private String clientid;
    private String username;
    private String peername;
    private Long connected_at;
    private Long disconnected_at;
    private String reason;
}
