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
 * 车辆车系
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tw_vehicle_series")
public class TwVehicleSeries extends Model<TwVehicleSeries> {

    /**
     * 主键标识
     */
    @TableId
    private Long id;
    /**
     * 车系编码
     */
    private String seriesCode;
    /**
     * 车系名称
     */
    private String seriesName;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 封面文件ID
     */
    private Long coverFileId;
    /**
     * 排序号
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
