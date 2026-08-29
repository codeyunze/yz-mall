package com.yz.mall.tw.vehicle.service;

import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleModelSlimVo;

/**
 * 车型跨服务扩展接口
 */
public interface ExtendTwVehicleModelService {

    /**
     * 按车型编码查摘要（含停用，供展示；status 字段标识状态）
     *
     * @param modelCode 车型编码
     * @return 摘要，不存在返回 null
     */
    ExtendTwVehicleModelSlimVo getByCode(String modelCode);

    /**
     * 是否存在且启用
     *
     * @param modelCode 车型编码
     * @return true=存在且启用
     */
    Boolean existsEnabled(String modelCode);
}
