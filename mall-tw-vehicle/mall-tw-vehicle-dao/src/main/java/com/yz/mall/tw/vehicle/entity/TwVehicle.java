package com.yz.mall.tw.vehicle.entity;

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
 * 车辆档案
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tw_vehicle")
public class TwVehicle extends Model<TwVehicle> {

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
     * 车牌号
     */
    private String plateNo;
    /**
     * 车型编码
     */
    private String modelCode;
    /**
     * 车型名称
     */
    private String modelName;
    /**
     * 车身颜色
     */
    private String color;
    /**
     * 启用状态：0停用 1启用
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 封面文件ID
     */
    private Long coverFileId;
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
     * 数据是否有效：0 有效；删除时置为行 id
     */
    @TableLogic(value = "0", delval = "1")
    private Long invalid;
}
