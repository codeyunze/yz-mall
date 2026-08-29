package com.yz.mall.tw.vehicle.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 编辑车型（不可改 modelCode / 不可跨车系）
 */
@Data
public class TwVehicleModelUpdateDto {

    /**
     * 车型ID
     */
    @NotNull(message = "车型ID不能为空")
    private Long id;
    /**
     * 车型名称
     */
    @Size(max = 64, message = "车型名称过长")
    private String modelName;
    /**
     * 能源类型
     */
    private Integer energyType;
    /**
     * 驱动类型
     */
    private Integer driveType;
    /**
     * 座位数
     */
    private Integer seatCount;
    /**
     * 电池容量
     */
    private BigDecimal batteryKwh;
    /**
     * 续航 km
     */
    private Integer rangeKm;
    /**
     * 封面文件ID
     */
    private Long coverFileId;
    /**
     * 排序号
     */
    private Integer sortNo;
    /**
     * 备注
     */
    private String remark;
}
