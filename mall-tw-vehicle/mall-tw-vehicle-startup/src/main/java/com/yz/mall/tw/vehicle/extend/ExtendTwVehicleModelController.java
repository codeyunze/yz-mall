package com.yz.mall.tw.vehicle.extend;

import com.yz.mall.base.Result;
import com.yz.mall.tw.vehicle.service.ExtendTwVehicleModelService;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleModelSlimVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 车型内部扩展接口
 */
@RestController
@RequestMapping("extend/tw/model")
public class ExtendTwVehicleModelController {

    private final ExtendTwVehicleModelService extendTwVehicleModelService;

    public ExtendTwVehicleModelController(ExtendTwVehicleModelService extendTwVehicleModelService) {
        this.extendTwVehicleModelService = extendTwVehicleModelService;
    }

    /**
     * 按车型编码查摘要
     */
    @GetMapping("by-code/{modelCode}")
    public Result<ExtendTwVehicleModelSlimVo> getByCode(@PathVariable String modelCode) {
        return Result.success(extendTwVehicleModelService.getByCode(modelCode));
    }

    /**
     * 是否存在且启用
     */
    @GetMapping("exists-enabled")
    public Result<Boolean> existsEnabled(@RequestParam String modelCode) {
        return Result.success(extendTwVehicleModelService.existsEnabled(modelCode));
    }
}
