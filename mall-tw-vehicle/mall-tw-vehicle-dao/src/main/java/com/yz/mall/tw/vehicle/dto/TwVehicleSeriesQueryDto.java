package com.yz.mall.tw.vehicle.dto;

import lombok.Data;

/**
 * 车系分页查询
 */
@Data
public class TwVehicleSeriesQueryDto {

    /**
     * 车系编码
     */
    private String seriesCode;
    /**
     * 车系名称（模糊）
     */
    private String seriesName;
    /**
     * 品牌名称（模糊）
     */
    private String brandName;
    /**
     * 启用状态
     */
    private Integer status;
}
