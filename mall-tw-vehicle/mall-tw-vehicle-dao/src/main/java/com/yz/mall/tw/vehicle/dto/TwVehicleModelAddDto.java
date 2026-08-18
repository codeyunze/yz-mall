package com.yz.mall.tw.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 新建车型
 */
@Data
public class TwVehicleModelAddDto {

    /**
     * 车系ID（与 seriesCode 二选一，优先 seriesId）
     */
    private Long seriesId;
    /**
     * 车系编码
     */
    private String seriesCode;
    /**
     * 车型编码
     */
    @NotBlank(message = "车型编码不能为空")
    @Size(min = 2, max = 64, message = "车型编码长度须在2~64之间")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "编码仅支持字母数字下划线")
    private String modelCode;
    /**
     * 车型名称
     */
    @NotBlank(message = "车型名称不能为空")
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
    /**
     * 启用状态
     */
    private Integer status;
}
