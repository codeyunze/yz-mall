package com.yz.mall.tw.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新建车辆
 */
@Data
public class TwVehicleAddDto {

    /**
     * 车架号 VIN
     */
    @NotBlank(message = "VIN不能为空")
    @Size(min = 11, max = 20, message = "VIN长度须在11~20之间")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "VIN格式不正确")
    private String vin;
    /**
     * 车牌号
     */
    private String plateNo;
    /**
     * 车型编码（关联 tw_vehicle_model.model_code，必填）
     */
    @NotBlank(message = "车型编码不能为空")
    private String modelCode;
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
    /**
     * 启用状态，默认1
     */
    private Integer status;
}
