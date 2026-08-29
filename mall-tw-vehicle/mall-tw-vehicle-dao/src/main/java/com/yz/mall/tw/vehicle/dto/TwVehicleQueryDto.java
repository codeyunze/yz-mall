package com.yz.mall.tw.vehicle.dto;

import lombok.Data;

/**
 * 车辆分页查询
 */
@Data
public class TwVehicleQueryDto {

    /**
     * VIN，右模糊
     */
    private String vin;
    /**
     * 车牌，模糊
     */
    private String plateNo;
    /**
     * 车系编码
     */
    private String seriesCode;
    /**
     * 车型编码
     */
    private String modelCode;
    /**
     * 启用状态：0停用 1启用
     */
    private Integer status;
    /**
     * 在线筛选：0离线 1在线
     */
    private Integer onlineStatus;
    /**
     * 按当前有效车主筛
     */
    private Long ownerUserId;
    /**
     * 按有效授权用户筛
     */
    private Long authUserId;
    /**
     * 端侧快捷筛：1我是车主 2我被授权
     */
    private Integer myRelation;
}
