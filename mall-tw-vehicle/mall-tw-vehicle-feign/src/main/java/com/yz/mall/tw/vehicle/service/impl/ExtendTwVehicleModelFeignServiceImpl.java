package com.yz.mall.tw.vehicle.service.impl;

import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.tw.vehicle.feign.ExtendTwVehicleModelFeign;
import com.yz.mall.tw.vehicle.service.ExtendTwVehicleModelService;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleModelSlimVo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * 车型跨服务扩展 — Feign 远程实现（仅当本域实现不在 classpath 时生效）
 */
@Service
@ConditionalOnMissingBean(name = "extendTwVehicleModelServiceImpl")
public class ExtendTwVehicleModelFeignServiceImpl implements ExtendTwVehicleModelService {

    private final ExtendTwVehicleModelFeign feign;

    public ExtendTwVehicleModelFeignServiceImpl(ExtendTwVehicleModelFeign feign) {
        this.feign = feign;
    }

    @Override
    public ExtendTwVehicleModelSlimVo getByCode(String modelCode) {
        Result<ExtendTwVehicleModelSlimVo> result = feign.getByCode(modelCode);
        assertSuccess(result);
        return result.getData();
    }

    @Override
    public Boolean existsEnabled(String modelCode) {
        Result<Boolean> result = feign.existsEnabled(modelCode);
        assertSuccess(result);
        return result.getData();
    }

    private static void assertSuccess(Result<?> result) {
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
    }
}
