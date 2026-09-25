package com.yz.mall.tw.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 轨迹折线单点
 */
@Data
public class TwGpsTrackPointVo {

    /**
     * 经度
     */
    private BigDecimal lng;
    /**
     * 纬度
     */
    private BigDecimal lat;
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
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime gpsTime;
}
