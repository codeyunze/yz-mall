package com.yz.mall.tw.vehicle.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 车辆详情
 */
@Data
public class TwVehicleDetailVo {

    private Long id;
    private String vin;
    private String plateNo;
    private String modelCode;
    private String modelName;
    private String color;
    private Integer status;
    private String remark;
    private Long coverFileId;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;
    private TwVehicleOwnerVo owner;
    private List<TwVehicleAuthVo> authUsers = new ArrayList<>();
    /**
     * 当前用户关系：0/1车主/2授权
     */
    private Integer myRelation;
    private Integer myAuthScope;
    private TwVehicleDeviceSummaryVo device;
    private TwVehicleLocationVo location;
    private Boolean online;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime lastOnlineTime;
}
