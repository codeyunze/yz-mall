package com.yz.mall.tw.device.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 终端-车辆绑定
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tw_device_vehicle")
public class TwDeviceVehicle extends Model<TwDeviceVehicle> {

    @TableId
    private Long id;
    /**
     * 终端主键
     */
    private Long devicePk;
    /**
     * 终端客户端ID
     */
    private String deviceId;
    /**
     * 车辆ID
     */
    private Long vehicleId;
    /**
     * VIN
     */
    private String vin;
    /**
     * 绑定状态：0已解绑 1绑定中
     */
    private Integer bindStatus;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime bindTime;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime unbindTime;
    private String remark;
    private Long createId;
    private Long updateId;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;
    @TableLogic(value = "0", delval = "1")
    private Long invalid;
}
