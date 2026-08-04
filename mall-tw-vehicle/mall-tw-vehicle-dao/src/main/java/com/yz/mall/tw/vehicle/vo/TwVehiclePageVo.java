package com.yz.mall.tw.vehicle.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 车辆分页行
 */
@Data
public class TwVehiclePageVo {

    private Long id;
    private String vin;
    private String plateNo;
    private String modelCode;
    private String modelName;
    private String color;
    private Integer status;
    private Long ownerUserId;
    private String ownerUsername;
    private Integer authUserCount;
    /**
     * 当前用户关系：0无 1车主 2授权
     */
    private Integer myRelation;
    private Boolean online;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime lastOnlineTime;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;
}
