package com.yz.mall.tw.vehicle.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 当前车主信息
 */
@Data
public class TwVehicleOwnerVo {

    private Long userId;
    private String username;
    private String nickname;
    private String phone;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime bindTime;
}
