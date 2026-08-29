package com.yz.mall.tw.vehicle.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 最新位置摘要（可读 Redis，P0 可为空）
 */
@Data
public class TwVehicleLocationVo {

    private BigDecimal lng;
    private BigDecimal lat;
    private BigDecimal speed;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime reportTime;
}
