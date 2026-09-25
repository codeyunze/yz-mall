package com.yz.mall.tw.controller;

import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.tw.dto.TwGpsLatestBatchQueryDto;
import com.yz.mall.tw.dto.TwGpsTrackQueryDto;
import com.yz.mall.tw.service.TwTelemetryService;
import com.yz.mall.tw.vo.TwGpsLatestVo;
import com.yz.mall.tw.vo.TwGpsTrackVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 遥测对外接口：最新位置 / 轨迹（经网关 /tw/telemetry/**）
 */
@RestController
@RequestMapping("tw/telemetry")
public class TwTelemetryController extends ApiController {

    private final TwTelemetryService telemetryService;

    public TwTelemetryController(TwTelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    /**
     * 单车最新位置（vin / vehicleId 二选一）
     */
    // @SaCheckPermission("api:tw:telemetry:latest")
    @GetMapping("latest")
    public Result<TwGpsLatestVo> latest(@RequestParam(required = false) String vin,
                                        @RequestParam(required = false) Long vehicleId) {
        return success(telemetryService.getLatest(vin, vehicleId));
    }

    /**
     * 批量最新位置（运营/监控，≤200）
     */
    // @SaCheckPermission("api:tw:telemetry:latest:batch")
    @PostMapping("latest/batch")
    public Result<List<TwGpsLatestVo>> batchLatest(@RequestBody TwGpsLatestBatchQueryDto dto) {
        return success(telemetryService.batchLatest(dto));
    }

    /**
     * 轨迹查询（时间窗 ≤ 配置小时数，默认 24h）
     */
    // @SaCheckPermission("api:tw:telemetry:track")
    @PostMapping("track")
    public Result<TwGpsTrackVo> track(@RequestBody @Valid TwGpsTrackQueryDto dto) {
        return success(telemetryService.queryTrack(dto));
    }
}
