package com.yz.mall.tw.dto;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 最新 GPS 写入（消费侧 / 节点 1 灌数入口）
 */
@Data
public class TwGpsLatestWriteDto {

    /**
     * 车架号 VIN
     */
    @NotBlank(message = "VIN不能为空")
    private String vin;
    /**
     * 车辆 ID，可选
     */
    private Long vehicleId;
    /**
     * 经度 WGS84
     */
    @NotNull(message = "经度不能为空")
    private BigDecimal lng;
    /**
     * 纬度 WGS84
     */
    @NotNull(message = "纬度不能为空")
    private BigDecimal lat;
    /**
     * 海拔米
     */
    private BigDecimal altitude;
    /**
     * 速度 km/h
     */
    private BigDecimal speed;
    /**
     * 航向角 0-360
     */
    private BigDecimal heading;
    /**
     * GPS 定位时间；空则用接收时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime gpsTime;
    /**
     * 电池 SOC%（P1）
     */
    private BigDecimal soc;
    /**
     * 信号强度（P1）
     */
    private Integer signalLevel;
    /**
     * 扩展 JSON 字符串
     */
    private String rawExt;
}
