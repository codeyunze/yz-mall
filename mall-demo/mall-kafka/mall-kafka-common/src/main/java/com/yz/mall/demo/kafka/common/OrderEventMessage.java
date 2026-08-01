package com.yz.mall.demo.kafka.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单事件消息体，Producer 与 Consumer 共用。
 */
public class OrderEventMessage implements Serializable {

    private final String orderId;
    private final String eventType;
    private final String payload;
    private final LocalDateTime eventTime;

    @JsonCreator
    public OrderEventMessage(
            @JsonProperty("orderId") String orderId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("payload") String payload,
            @JsonProperty("eventTime") LocalDateTime eventTime) {
        this.orderId = orderId;
        this.eventType = eventType;
        this.payload = payload;
        this.eventTime = eventTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    @Override
    public String toString() {
        return "OrderEventMessage{orderId='" + orderId + "', eventType='" + eventType
                + "', payload='" + payload + "', eventTime=" + eventTime + '}';
    }
}
