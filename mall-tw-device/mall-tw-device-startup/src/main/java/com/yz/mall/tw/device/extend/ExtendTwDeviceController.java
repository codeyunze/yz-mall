package com.yz.mall.tw.device.extend;

import com.yz.mall.base.Result;
import com.yz.mall.tw.device.dto.TwDeviceVerifyDto;
import com.yz.mall.tw.device.service.ExtendTwDeviceService;
import com.yz.mall.tw.device.vo.ExtendTwDeviceAuthVo;
import com.yz.mall.tw.device.vo.ExtendTwDeviceSlimVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 终端内部扩展接口（供 access / vehicle）
 */
@RestController
@RequestMapping("extend/tw/device")
public class ExtendTwDeviceController {

    private final ExtendTwDeviceService extendTwDeviceService;

    public ExtendTwDeviceController(ExtendTwDeviceService extendTwDeviceService) {
        this.extendTwDeviceService = extendTwDeviceService;
    }

    @GetMapping("auth/{deviceId}")
    public Result<ExtendTwDeviceAuthVo> auth(@PathVariable String deviceId) {
        return Result.success(extendTwDeviceService.getAuthByDeviceId(deviceId));
    }

    @GetMapping("by-vin/{vin}")
    public Result<ExtendTwDeviceSlimVo> byVin(@PathVariable String vin) {
        return Result.success(extendTwDeviceService.getByVin(vin));
    }

    @GetMapping("{deviceId}/bound-vin")
    public Result<String> boundVin(@PathVariable String deviceId) {
        return Result.success(extendTwDeviceService.getBoundVin(deviceId));
    }

    @PostMapping("verify")
    public Result<Boolean> verify(@RequestBody @Valid TwDeviceVerifyDto dto) {
        return Result.success(extendTwDeviceService.verify(dto.getDeviceId(), dto.getPassword()));
    }
}
