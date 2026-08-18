package com.yz.mall.tw.vehicle.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑车系（不可改编码）
 */
@Data
public class TwVehicleSeriesUpdateDto {

    /**
     * 车系ID
     */
    @NotNull(message = "车系ID不能为空")
    private Long id;
    /**
     * 车系名称
     */
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
}
