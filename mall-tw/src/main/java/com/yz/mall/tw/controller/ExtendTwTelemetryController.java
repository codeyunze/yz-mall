package com.yz.mall.tw.controller;

import com.yz.mall.base.Result;
import com.yz.mall.tw.service.TwTelemetryService;
import com.yz.mall.tw.vo.TwGpsLatestVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 遥测内部扩展接口（供车辆详情等聚合）
 */
@RestController
@RequestMapping("extend/tw/telemetry")
public class ExtendTwTelemetryController {

    private final TwTelemetryService telemetryService;

    public ExtendTwTelemetryController(TwTelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    /**
     * 按 VIN 取最新位置摘要
     */
    @GetMapping("latest/{vin}")
    public Result<TwGpsLatestVo> latestByVin(@PathVariable String vin) {
        return Result.success(telemetryService.getLatestForExtend(vin));
    }
}
