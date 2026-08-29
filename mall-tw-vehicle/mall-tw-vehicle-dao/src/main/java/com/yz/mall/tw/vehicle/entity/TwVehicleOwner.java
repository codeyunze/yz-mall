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
 * 车辆车主绑定
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tw_vehicle_owner")
public class TwVehicleOwner extends Model<TwVehicleOwner> {

    /**
     * 主键标识
     */
    @TableId
    private Long id;
    /**
     * 车辆ID
     */
    private Long vehicleId;
    /**
     * VIN 冗余
     */
    private String vin;
    /**
     * 车主用户ID
     */
    private Long ownerUserId;
    /**
     * 绑定状态：0已解绑 1绑定中
     */
    private Integer bindStatus;
    /**
     * 绑定时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime bindTime;
    /**
     * 解绑时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime unbindTime;
    /**
     * 来源：1运营绑定 2过户 3自助
     */
    private Integer bindSource;
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
     * 数据是否有效：0数据有效
     */
    @TableLogic(value = "0", delval = "1")
    private Long invalid;
}
