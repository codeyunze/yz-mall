package com.yz.mall.tw.dto;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 轨迹查询入参
 */
@Data
public class TwGpsTrackQueryDto {

    /**
     * 车架号（与 vehicleId 二选一）
     */
    private String vin;
    /**
     * 车辆 ID
     */
    private Long vehicleId;
    /**
     * 开始时间（含）
     */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime startTime;
    /**
     * 结束时间（含）
     */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime endTime;
    /**
     * 最大返回点数；空则用配置默认值
     */
    private Integer maxPoints;
}
