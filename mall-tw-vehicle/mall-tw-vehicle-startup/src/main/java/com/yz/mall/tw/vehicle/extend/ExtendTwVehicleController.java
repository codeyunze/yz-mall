package com.yz.mall.tw.vehicle.extend;

import com.yz.mall.base.Result;
import com.yz.mall.tw.vehicle.service.ExtendTwVehicleService;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleAccessVo;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleSlimVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 车辆档案内部扩展接口
 */
@RestController
@RequestMapping("extend/tw/vehicle")
public class ExtendTwVehicleController {

    private final ExtendTwVehicleService extendTwVehicleService;

    public ExtendTwVehicleController(ExtendTwVehicleService extendTwVehicleService) {
        this.extendTwVehicleService = extendTwVehicleService;
    }

    /**
     * 按 VIN 查有效车辆摘要
     */
    @GetMapping("by-vin/{vin}")
    public Result<ExtendTwVehicleSlimVo> getByVin(@PathVariable String vin) {
        return Result.success(extendTwVehicleService.getByVin(vin));
    }

    /**
     * 按主键查有效车辆摘要
     */
    @GetMapping("by-id/{id}")
    public Result<ExtendTwVehicleSlimVo> getById(@PathVariable Long id) {
        return Result.success(extendTwVehicleService.getById(id));
    }

    /**
     * 是否存在且启用
     */
    @GetMapping("{id}/exists-enabled")
    public Result<Boolean> existsEnabled(@PathVariable Long id) {
        return Result.success(extendTwVehicleService.existsEnabled(id));
    }

    /**
     * 校验用户对车辆的访问能力
     */
    @GetMapping("access/check")
    public Result<ExtendTwVehicleAccessVo> checkAccess(@RequestParam(required = false) Long vehicleId,
                                                       @RequestParam(required = false) String vin,
                                                       @RequestParam Long userId,
                                                       @RequestParam(required = false) Integer scope) {
        return Result.success(extendTwVehicleService.checkAccess(vehicleId, vin, userId, scope));
    }
}
