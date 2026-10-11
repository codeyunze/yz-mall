package com.yz.mall.tw.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Kafka {@code tw-telemetry-raw} 原始 GPS 消息（字段名与 access 生产约定一致）。
 */
@Data
public class TwTelemetryRawMessage {

    /**
     * 车架号（也可由 Kafka Key 补全）
     */
    private String vin;
    /**
     * 车辆 ID，可选
     */
    private Long vehicleId;
    /**
     * 经度
     */
    private BigDecimal lng;
    /**
     * 纬度
     */
    private BigDecimal lat;
    /**
     * 海拔
     */
    private BigDecimal altitude;
    /**
     * 速度 km/h
     */
    private BigDecimal speed;
    /**
     * 航向
     */
    private BigDecimal heading;
    /**
     * GPS 时间字符串（支持 ISO-8601 或 yyyy-MM-dd HH:mm:ss）
     */
    private String gpsTime;
    /**
     * 电池 SOC
     */
    private BigDecimal soc;
    /**
     * 信号强度
     */
    private Integer signalLevel;
}
