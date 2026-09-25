package com.yz.mall.tw.controller;

import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.tw.dto.TwGpsLatestWriteDto;
import com.yz.mall.tw.service.TwGpsLatestService;
import com.yz.mall.tw.vo.TwGpsLatestVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 遥测节点 1 临时灌数 / 读校验入口（无 Kafka 时验证 Latest 写读；节点 2 正式 API 另立路径）。
 */
@RestController
@RequestMapping("tw/telemetry/dev")
public class TwTelemetryDevController extends ApiController {

    private final TwGpsLatestService gpsLatestService;

    public TwTelemetryDevController(TwGpsLatestService gpsLatestService) {
        this.gpsLatestService = gpsLatestService;
    }

    /**
     * 写入一条假 GPS（Redis + MySQL UPSERT）
     */
    @PostMapping("ingest")
    public Result<TwGpsLatestVo> ingest(@RequestBody @Valid TwGpsLatestWriteDto dto) {
        TwGpsLatestVo vo = gpsLatestService.saveLatest(dto);
        if (vo == null) {
            throw new BusinessException("坐标非法或写入失败");
        }
        return success(vo);
    }

    /**
     * 按 VIN 读最新位置（Redis → MySQL 回填）
     */
    @GetMapping("latest")
    public Result<TwGpsLatestVo> latest(@RequestParam String vin) {
        return success(gpsLatestService.getLatestByVin(vin));
    }
}
