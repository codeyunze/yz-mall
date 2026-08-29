package com.yz.mall.tw.device.feign;

import com.yz.mall.base.Result;
import com.yz.mall.tw.device.dto.TwDeviceVerifyDto;
import com.yz.mall.tw.device.vo.ExtendTwDeviceAuthVo;
import com.yz.mall.tw.device.vo.ExtendTwDeviceSlimVo;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 终端 Feign
 */
@Repository
@FeignClient(name = "tw-device", contextId = "extendTwDevice", path = "extend/tw/device")
public interface ExtendTwDeviceFeign {

    @GetMapping("auth/{deviceId}")
    Result<ExtendTwDeviceAuthVo> auth(@PathVariable("deviceId") String deviceId);

    @GetMapping("by-vin/{vin}")
    Result<ExtendTwDeviceSlimVo> byVin(@PathVariable("vin") String vin);

    @GetMapping("{deviceId}/bound-vin")
    Result<String> boundVin(@PathVariable("deviceId") String deviceId);

    @PostMapping("verify")
    Result<Boolean> verify(@RequestBody @Valid TwDeviceVerifyDto dto);
}
