package com.yz.mall.tw.vehicle.feign;

import com.yz.mall.base.Result;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleModelSlimVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 车型 Feign
 */
@Repository
@FeignClient(name = "tw-vehicle", contextId = "extendTwVehicleModel", path = "extend/tw/model")
public interface ExtendTwVehicleModelFeign {

    /**
     * 按车型编码查摘要
     */
    @GetMapping("by-code/{modelCode}")
    Result<ExtendTwVehicleModelSlimVo> getByCode(@PathVariable("modelCode") String modelCode);

    /**
     * 是否存在且启用
     */
    @GetMapping("exists-enabled")
    Result<Boolean> existsEnabled(@RequestParam("modelCode") String modelCode);
}
