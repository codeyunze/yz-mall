package com.yz.mall.tw.vehicle.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yz.mall.tw.vehicle.constant.TwVehicleConstants;
import com.yz.mall.tw.vehicle.entity.TwVehicle;
import com.yz.mall.tw.vehicle.entity.TwVehicleAuth;
import com.yz.mall.tw.vehicle.entity.TwVehicleOwner;
import com.yz.mall.tw.vehicle.service.ExtendTwVehicleService;
import com.yz.mall.tw.vehicle.service.TwVehicleAuthService;
import com.yz.mall.tw.vehicle.service.TwVehicleOwnerService;
import com.yz.mall.tw.vehicle.service.TwVehicleService;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleAccessVo;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleSlimVo;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 车辆档案跨服务扩展 — 本域实现
 */
@Service
public class ExtendTwVehicleServiceImpl implements ExtendTwVehicleService {

    private final TwVehicleService vehicleService;
    private final TwVehicleOwnerService ownerService;
    private final TwVehicleAuthService authService;

    public ExtendTwVehicleServiceImpl(TwVehicleService vehicleService, TwVehicleOwnerService ownerService, TwVehicleAuthService authService) {
        this.vehicleService = vehicleService;
        this.ownerService = ownerService;
        this.authService = authService;
    }

    @Override
    public ExtendTwVehicleSlimVo getByVin(String vin) {
        if (StrUtil.isBlank(vin)) {
            return null;
        }
        TwVehicle vehicle = vehicleService.getOne(new LambdaQueryWrapper<TwVehicle>().eq(TwVehicle::getVin, vin.trim().toUpperCase()).last("limit 1"), false);
        if (vehicle == null) {
            return null;
        }
        ExtendTwVehicleSlimVo vo = new ExtendTwVehicleSlimVo();
        BeanUtil.copyProperties(vehicle, vo);
        return vo;
    }

    @Override
    public ExtendTwVehicleSlimVo getById(Long id) {
        if (id == null) {
            return null;
        }
        TwVehicle vehicle = vehicleService.getById(id);
        if (vehicle == null) {
            return null;
        }
        ExtendTwVehicleSlimVo vo = new ExtendTwVehicleSlimVo();
        BeanUtil.copyProperties(vehicle, vo);
        return vo;
    }

    @Override
    public Boolean existsEnabled(Long id) {
        if (id == null) {
            return false;
        }
        TwVehicle vehicle = vehicleService.getById(id);
        return vehicle != null && Objects.equals(vehicle.getStatus(), TwVehicleConstants.STATUS_ENABLED);
    }

    @Override
    public ExtendTwVehicleAccessVo checkAccess(Long vehicleId, String vin, Long userId, Integer scope) {
        ExtendTwVehicleAccessVo result = new ExtendTwVehicleAccessVo();
        result.setAllowed(false);
        result.setRelation(TwVehicleConstants.RELATION_NONE);
        result.setAuthScope(0);
        if (userId == null) {
            return result;
        }
        TwVehicle vehicle = null;
        if (vehicleId != null) {
            vehicle = vehicleService.getById(vehicleId);
        } else if (StrUtil.isNotBlank(vin)) {
            vehicle = vehicleService.getOne(new LambdaQueryWrapper<TwVehicle>().eq(TwVehicle::getVin, vin.trim().toUpperCase()).last("limit 1"), false);
        }
        if (vehicle == null) {
            return result;
        }
        TwVehicleOwner owner = ownerService.getActiveOwner(vehicle.getId());
        if (owner != null && Objects.equals(owner.getOwnerUserId(), userId)) {
            result.setAllowed(true);
            result.setRelation(TwVehicleConstants.RELATION_OWNER);
            result.setAuthScope(TwVehicleConstants.SCOPE_ALL);
            return result;
        }
        TwVehicleAuth auth = authService.getActiveAuth(vehicle.getId(), userId);
        if (auth == null) {
            return result;
        }
        result.setRelation(TwVehicleConstants.RELATION_AUTH);
        result.setAuthScope(auth.getAuthScope());
        int need = scope == null ? TwVehicleConstants.SCOPE_VIEW : scope;
        result.setAllowed((auth.getAuthScope() & need) == need);
        return result;
    }
}
