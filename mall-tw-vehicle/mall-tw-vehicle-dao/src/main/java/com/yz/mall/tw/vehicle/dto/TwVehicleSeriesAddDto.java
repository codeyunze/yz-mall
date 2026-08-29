package com.yz.mall.tw.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新建车系
 */
@Data
public class TwVehicleSeriesAddDto {

    /**
     * 车系编码
     */
    @NotBlank(message = "车系编码不能为空")
    @Size(min = 2, max = 64, message = "车系编码长度须在2~64之间")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "编码仅支持字母数字下划线")
    private String seriesCode;
    /**
     * 车系名称
     */
    @NotBlank(message = "车系名称不能为空")
    @Size(max = 64, message = "车系名称过长")
    private String seriesName;
    /**
     * 品牌名称
     */
    private String brandName;
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
     * 启用状态，默认1
     */
    private Integer status;
}
