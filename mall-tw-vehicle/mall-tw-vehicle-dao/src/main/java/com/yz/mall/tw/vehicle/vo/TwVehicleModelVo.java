package com.yz.mall.tw.vehicle.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车型分页/详情
 */
@Data
public class TwVehicleModelVo {

    private Long id;
    private Long seriesId;
    private String seriesCode;
    private String seriesName;
    private String modelCode;
    private String modelName;
    private Integer energyType;
    private Integer driveType;
    private Integer seatCount;
    private BigDecimal batteryKwh;
    private Integer rangeKm;
    private Long coverFileId;
    private Integer sortNo;
    private Integer status;
    private String remark;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;
}
