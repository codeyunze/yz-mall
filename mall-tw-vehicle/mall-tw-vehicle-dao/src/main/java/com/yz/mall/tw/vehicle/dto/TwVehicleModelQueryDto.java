package com.yz.mall.tw.vehicle.dto;

import lombok.Data;

/**
 * 车型分页查询
 */
@Data
public class TwVehicleModelQueryDto {

    /**
     * 车系ID
     */
    private Long seriesId;
    /**
     * 车系编码
     */
    private String seriesCode;
    /**
     * 车型编码
     */
    private String modelCode;
    /**
     * 车型名称（模糊）
     */
    private String modelName;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 能源类型
     */
    private Integer energyType;
}
