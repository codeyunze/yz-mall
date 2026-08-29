package com.yz.mall.tw.vehicle.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 车辆档案业务配置
 */
@Data
@ConfigurationProperties(prefix = "tw.vehicle")
public class TwVehicleProperties {

    /**
     * 单车同时有效授权用户上限
     */
    private int maxAuthUsers = 10;
}
