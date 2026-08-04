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
 * 车辆授权用户
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tw_vehicle_auth")
public class TwVehicleAuth extends Model<TwVehicleAuth> {

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
     * 授权时车主用户ID
     */
    private Long ownerUserId;
    /**
     * 被授权用户ID
     */
    private Long authUserId;
    /**
     * 授权范围位掩码：1查看 2位置 4控车
     */
    private Integer authScope;
    /**
     * 授权状态：0已撤销 1有效
     */
    private Integer authStatus;
    /**
     * 授权时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime grantTime;
    /**
     * 过期时间，空=长期
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime expireTime;
    /**
     * 撤销时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime revokeTime;
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
