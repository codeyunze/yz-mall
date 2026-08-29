package com.yz.mall.tw.vehicle.dto;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 授权用户
 */
@Data
public class TwVehicleAuthGrantDto {

    /**
     * 车辆ID
     */
    @NotNull(message = "车辆ID不能为空")
    private Long vehicleId;
    /**
     * 被授权用户ID
     */
    @NotNull(message = "被授权用户ID不能为空")
    private Long authUserId;
    /**
     * 授权范围，默认 3=查看+位置
     */
    private Integer authScope;
    /**
     * 过期时间，空=长期
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime expireTime;
    /**
     * 备注
     */
    private String remark;
}
