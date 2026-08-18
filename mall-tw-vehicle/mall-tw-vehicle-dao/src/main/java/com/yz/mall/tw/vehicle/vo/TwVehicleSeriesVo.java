package com.yz.mall.tw.vehicle.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 车系分页/详情
 */
@Data
public class TwVehicleSeriesVo {

    private Long id;
    private String seriesCode;
    private String seriesName;
    private String brandName;
    private Long coverFileId;
    private Integer sortNo;
    private Integer status;
    private String remark;
    /**
     * 有效车型数
     */
    private Integer modelCount;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;
}
