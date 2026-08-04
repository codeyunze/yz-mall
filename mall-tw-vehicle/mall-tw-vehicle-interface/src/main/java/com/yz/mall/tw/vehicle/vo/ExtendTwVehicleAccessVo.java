package com.yz.mall.tw.vehicle.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户对车辆访问能力校验结果
 */
@Data
public class ExtendTwVehicleAccessVo implements Serializable {

    /**
     * 是否允许
     */
    private Boolean allowed;
    /**
     * 关系：0无 1车主 2授权
     */
    private Integer relation;
    /**
     * 有效授权范围（车主可视为全权限）
     */
    private Integer authScope;
}
