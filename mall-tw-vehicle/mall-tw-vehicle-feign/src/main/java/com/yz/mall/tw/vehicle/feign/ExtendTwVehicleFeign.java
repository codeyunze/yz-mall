package com.yz.mall.tw.vehicle.feign;

import com.yz.mall.base.Result;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleAccessVo;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleSlimVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 车辆档案 Feign
 */
@Repository
@FeignClient(name = "tw-vehicle", contextId = "extendTwVehicle", path = "extend/tw/vehicle")
public interface ExtendTwVehicleFeign {

    /**
     * 按 VIN 查有效车辆摘要
     */
    @GetMapping("by-vin/{vin}")
    Result<ExtendTwVehicleSlimVo> getByVin(@PathVariable("vin") String vin);

    /**
     * 按主键查有效车辆摘要
     */
    @GetMapping("by-id/{id}")
    Result<ExtendTwVehicleSlimVo> getById(@PathVariable("id") Long id);

    /**
     * 是否存在且启用
     */
    @GetMapping("{id}/exists-enabled")
    Result<Boolean> existsEnabled(@PathVariable("id") Long id);

    /**
     * 校验用户对车辆的访问能力
     */
    @GetMapping("access/check")
    Result<ExtendTwVehicleAccessVo> checkAccess(@RequestParam(value = "vehicleId", required = false) Long vehicleId,
                                                @RequestParam(value = "vin", required = false) String vin,
                                                @RequestParam("userId") Long userId,
                                                @RequestParam(value = "scope", required = false) Integer scope);
}
