package com.yz.mall.tw.vehicle.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 编辑车辆（VIN 不可改）
 */
@Data
public class TwVehicleUpdateDto {

    /**
     * 车辆ID
     */
    @NotNull(message = "车辆ID不能为空")
    private Long id;
    /**
     * 车牌号
     */
    private String plateNo;
    /**
     * 车型编码
     */
    private String modelCode;
    /**
     * 车型名称
     */
    private String modelName;
    /**
     * 颜色
     */
    private String color;
    /**
     * 备注
     */
    private String remark;
    /**
     * 封面文件ID
     */
    private Long coverFileId;
}
