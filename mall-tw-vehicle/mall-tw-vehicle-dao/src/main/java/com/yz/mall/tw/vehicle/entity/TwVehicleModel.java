package com.yz.mall.tw.vehicle.entity;

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
 * 车辆车型
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tw_vehicle_model")
public class TwVehicleModel extends Model<TwVehicleModel> {

    /**
     * 主键标识
     */
    @TableId
    private Long id;
    /**
     * 车系ID
     */
    private Long seriesId;
    /**
     * 车系编码冗余
     */
    private String seriesCode;
    /**
     * 车型编码
     */
    private String modelCode;
    /**
     * 车型名称
     */
    private String modelName;
    /**
     * 能源类型：1纯电 2插混 3增程 4燃油 9其他
     */
    private Integer energyType;
    /**
     * 驱动：1两驱 2四驱 9其他
     */
    private Integer driveType;
    /**
     * 座位数
     */
    private Integer seatCount;
    /**
     * 电池容量 kWh
     */
    private BigDecimal batteryKwh;
    /**
     * 工况续航 km
     */
    private Integer rangeKm;
    /**
     * 封面文件ID
     */
    private Long coverFileId;
    /**
     * 同车系内排序
     */
    private Integer sortNo;
    /**
     * 启用状态：0停用 1启用
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
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
     * 数据是否有效：0有效
     */
    @TableLogic(value = "0", delval = "1")
    private Long invalid;
}
