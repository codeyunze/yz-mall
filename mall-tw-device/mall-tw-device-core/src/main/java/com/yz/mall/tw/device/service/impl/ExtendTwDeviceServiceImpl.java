package com.yz.mall.tw.device.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yz.mall.tw.device.constant.TwDeviceConstants;
import com.yz.mall.tw.device.entity.TwDevice;
import com.yz.mall.tw.device.entity.TwDeviceVehicle;
import com.yz.mall.tw.device.service.ExtendTwDeviceService;
import com.yz.mall.tw.device.service.TwDeviceService;
import com.yz.mall.tw.device.vo.ExtendTwDeviceAuthVo;
import com.yz.mall.tw.device.vo.ExtendTwDeviceSlimVo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 终端跨服务扩展 — 本域实现
 */
@Service
public class ExtendTwDeviceServiceImpl implements ExtendTwDeviceService {

    private final TwDeviceService deviceService;
    private final PasswordEncoder passwordEncoder;

    public ExtendTwDeviceServiceImpl(TwDeviceService deviceService, PasswordEncoder passwordEncoder) {
        this.deviceService = deviceService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ExtendTwDeviceAuthVo getAuthByDeviceId(String deviceId) {
        if (StrUtil.isBlank(deviceId)) {
            return null;
        }
        TwDevice device = deviceService.getOne(new LambdaQueryWrapper<TwDevice>().eq(TwDevice::getDeviceId, deviceId.trim()).last("limit 1"), false);
        if (device == null) {
            return null;
        }
        ExtendTwDeviceAuthVo vo = new ExtendTwDeviceAuthVo();
        vo.setDeviceId(device.getDeviceId());
        vo.setEnabled(Objects.equals(device.getStatus(), TwDeviceConstants.STATUS_ENABLED));
        vo.setSecretHash(device.getSecretHash());
        vo.setSecretAlgo(device.getSecretAlgo());
        vo.setSecretSalt(device.getSecretSalt());
        TwDeviceVehicle bind = deviceService.getActiveBindByDevicePk(device.getId());
        if (bind != null) {
            vo.setBound(true);
            vo.setVin(bind.getVin());
            vo.setVehicleId(bind.getVehicleId());
        } else {
            vo.setBound(false);
        }
        return vo;
    }

    @Override
    public ExtendTwDeviceSlimVo getByVin(String vin) {
        if (StrUtil.isBlank(vin)) {
            return null;
        }
        TwDeviceVehicle bind = deviceService.getActiveBindByVin(vin);
        if (bind == null) {
            return null;
        }
        TwDevice device = deviceService.getById(bind.getDevicePk());
        if (device == null) {
            return null;
        }
        ExtendTwDeviceSlimVo vo = new ExtendTwDeviceSlimVo();
        BeanUtil.copyProperties(device, vo);
        vo.setVehicleId(bind.getVehicleId());
        vo.setVin(bind.getVin());
        return vo;
    }

    @Override
    public String getBoundVin(String deviceId) {
        if (StrUtil.isBlank(deviceId)) {
            return null;
        }
        TwDevice device = deviceService.getOne(new LambdaQueryWrapper<TwDevice>().eq(TwDevice::getDeviceId, deviceId.trim()).last("limit 1"), false);
        if (device == null) {
            return null;
        }
        TwDeviceVehicle bind = deviceService.getActiveBindByDevicePk(device.getId());
        return bind == null ? null : bind.getVin();
    }

    @Override
    public Boolean verify(String deviceId, String password) {
        ExtendTwDeviceAuthVo auth = getAuthByDeviceId(deviceId);
        if (auth == null || !Boolean.TRUE.equals(auth.getEnabled()) || !Boolean.TRUE.equals(auth.getBound())) {
            return false;
        }
        if (StrUtil.isBlank(password) || StrUtil.isBlank(auth.getSecretHash())) {
            return false;
        }
        return passwordEncoder.matches(password, auth.getSecretHash());
    }
}
