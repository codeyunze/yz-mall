package com.yz.mall.tw.dto;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 轨迹点（写入缓冲 / CH 行）
 */
@Data
public class TwGpsTrackPointDto {

    /**
     * 车架号
     */
    @NotBlank(message = "VIN不能为空")
    private String vin;
    /**
     * 车辆 ID
     */
    private Long vehicleId;
    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    private BigDecimal lng;
    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    private BigDecimal lat;
    /**
     * 海拔
     */
    private BigDecimal altitude;
    /**
     * 速度
     */
    private BigDecimal speed;
    /**
     * 航向
     */
    private BigDecimal heading;
    /**
     * GPS 时间
     */
    @NotNull(message = "GPS时间不能为空")
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime gpsTime;
    /**
     * 接收时间；空则写入时补 now
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime receiveTime;
    /**
     * SOC
     */
    private BigDecimal soc;
    /**
     * 信号强度
     */
    private Integer signalLevel;
}
