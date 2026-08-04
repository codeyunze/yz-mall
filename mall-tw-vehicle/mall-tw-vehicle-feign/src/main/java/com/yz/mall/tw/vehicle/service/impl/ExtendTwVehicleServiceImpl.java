package com.yz.mall.tw.vehicle.service.impl;

import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.tw.vehicle.feign.ExtendTwVehicleFeign;
import com.yz.mall.tw.vehicle.service.ExtendTwVehicleService;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleAccessVo;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleSlimVo;
import org.springframework.stereotype.Service;

/**
 * 车辆档案跨服务扩展 — Feign 远程实现
 */
@Service
public class ExtendTwVehicleServiceImpl implements ExtendTwVehicleService {

    private final ExtendTwVehicleFeign feign;

    public ExtendTwVehicleServiceImpl(ExtendTwVehicleFeign feign) {
        this.feign = feign;
    }

    @Override
    public ExtendTwVehicleSlimVo getByVin(String vin) {
        Result<ExtendTwVehicleSlimVo> result = feign.getByVin(vin);
        assertSuccess(result);
        return result.getData();
    }

    @Override
    public ExtendTwVehicleSlimVo getById(Long id) {
        Result<ExtendTwVehicleSlimVo> result = feign.getById(id);
        assertSuccess(result);
        return result.getData();
    }

    @Override
    public Boolean existsEnabled(Long id) {
        Result<Boolean> result = feign.existsEnabled(id);
        assertSuccess(result);
        return result.getData();
    }

    @Override
    public ExtendTwVehicleAccessVo checkAccess(Long vehicleId, String vin, Long userId, Integer scope) {
        Result<ExtendTwVehicleAccessVo> result = feign.checkAccess(vehicleId, vin, userId, scope);
        assertSuccess(result);
        return result.getData();
    }

    private static void assertSuccess(Result<?> result) {
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
    }
}
