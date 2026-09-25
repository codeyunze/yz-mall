package com.yz.mall.tw.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 最新 GPS 位置出参
 */
@Data
public class TwGpsLatestVo {

    /**
     * 车架号
     */
    private String vin;
    /**
     * 车辆 ID
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
     * 航向角
     */
    private BigDecimal heading;
    /**
     * GPS 时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime gpsTime;
    /**
     * 云端接收时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime receiveTime;
    /**
     * 是否在线（可选，节点 1 可不填）
     */
    private Boolean online;
    /**
     * 电池 SOC%（P1）
     */
    private BigDecimal soc;
    /**
     * 信号强度（P1）
     */
    private Integer signalLevel;
}
