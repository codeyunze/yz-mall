package com.yz.mall.tw.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车辆最新 GPS 位置（MySQL 兜底，每 VIN 一行）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tw_gps_latest")
public class TwGpsLatest extends Model<TwGpsLatest> {

    /**
     * 主键标识
     */
    @TableId
    private Long id;
    /**
     * 车架号 VIN
     */
    private String vin;
    /**
     * 车辆 ID 冗余
     */
    private Long vehicleId;
    /**
     * 经度 WGS84
     */
    private BigDecimal lng;
    /**
     * 纬度 WGS84
     */
    private BigDecimal lat;
    /**
     * 海拔（米）
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
     * GPS 定位时间（车机侧）
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime gpsTime;
    /**
     * 云端接收时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime receiveTime;
    /**
     * 电池 SOC%（P1）
     */
    private BigDecimal soc;
    /**
     * 信号强度等级（P1）
     */
    private Integer signalLevel;
    /**
     * 扩展字段 JSON
     */
    private String rawExt;
    /**
     * 创建人
     */
    private Long createId;
    /**
     * 更新人
     */
    private Long updateId;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;
    /**
     * 数据是否有效：0 有效
     */
    @TableLogic(value = "0", delval = "1")
    private Long invalid;
}
