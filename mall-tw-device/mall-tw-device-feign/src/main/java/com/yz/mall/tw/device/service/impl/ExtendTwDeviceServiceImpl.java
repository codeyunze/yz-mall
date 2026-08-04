package com.yz.mall.tw.device.service.impl;

import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.tw.device.feign.ExtendTwDeviceFeign;
import com.yz.mall.tw.device.service.ExtendTwDeviceService;
import com.yz.mall.tw.device.vo.ExtendTwDeviceAuthVo;
import com.yz.mall.tw.device.vo.ExtendTwDeviceSlimVo;
import org.springframework.stereotype.Service;

/**
 * 终端跨服务扩展 — Feign 远程实现
 */
@Service
public class ExtendTwDeviceServiceImpl implements ExtendTwDeviceService {

    private final ExtendTwDeviceFeign feign;

    public ExtendTwDeviceServiceImpl(ExtendTwDeviceFeign feign) {
        this.feign = feign;
    }

    @Override
    public ExtendTwDeviceAuthVo getAuthByDeviceId(String deviceId) {
        Result<ExtendTwDeviceAuthVo> result = feign.auth(deviceId);
        assertSuccess(result);
        return result.getData();
    }

    @Override
    public ExtendTwDeviceSlimVo getByVin(String vin) {
        Result<ExtendTwDeviceSlimVo> result = feign.byVin(vin);
        assertSuccess(result);
        return result.getData();
    }

    @Override
    public String getBoundVin(String deviceId) {
        Result<String> result = feign.boundVin(deviceId);
        assertSuccess(result);
        return result.getData();
    }

    @Override
    public Boolean verify(String deviceId, String password) {
        com.yz.mall.tw.device.dto.TwDeviceVerifyDto dto = new com.yz.mall.tw.device.dto.TwDeviceVerifyDto();
        dto.setDeviceId(deviceId);
        dto.setPassword(password);
        Result<Boolean> result = feign.verify(dto);
        assertSuccess(result);
        return result.getData();
    }

    private static void assertSuccess(Result<?> result) {
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
    }
}
