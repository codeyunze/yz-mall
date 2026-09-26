package com.yz.mall.tw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.tw.dto.TwGpsLatestWriteDto;
import com.yz.mall.tw.dto.TwGpsTrackPointDto;
import com.yz.mall.tw.dto.TwTelemetryRawMessage;
import com.yz.mall.tw.service.TwGpsLatestService;
import com.yz.mall.tw.service.TwGpsTrackService;
import com.yz.mall.tw.service.TwTelemetryIngestService;
import com.yz.mall.tw.vo.TwGpsLatestVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 遥测临时灌数入口（无 Kafka / 联调三写时使用）。
 */
@RestController
@RequestMapping("tw/telemetry/dev")
public class TwTelemetryDevController extends ApiController {

    private static final ObjectMapper OBJECT_MAPPER = JacksonUtil.getObjectMapper();

    private final TwGpsLatestService gpsLatestService;
    private final TwGpsTrackService gpsTrackService;
    private final TwTelemetryIngestService ingestService;

    public TwTelemetryDevController(TwGpsLatestService gpsLatestService, TwGpsTrackService gpsTrackService,
                                    TwTelemetryIngestService ingestService) {
        this.gpsLatestService = gpsLatestService;
        this.gpsTrackService = gpsTrackService;
        this.ingestService = ingestService;
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
     * 模拟 Kafka 原始消息三写（Latest + 可选 CH 缓冲）
     */
    @PostMapping("raw/ingest")
    public Result<Boolean> rawIngest(@RequestBody TwTelemetryRawMessage message,
                                     @RequestParam(required = false) String key) {
        try {
            String payload = OBJECT_MAPPER.writeValueAsString(message);
            boolean ok = ingestService.handleRaw(payload, key);
            if (!ok) {
                throw new BusinessException("消息丢弃或写入失败");
            }
            return success(true);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("模拟消费失败: " + ex.getMessage());
        }
    }

    /**
     * 按 VIN 读最新位置（Redis → MySQL 回填）
     */
    @GetMapping("latest")
    public Result<TwGpsLatestVo> latest(@RequestParam String vin) {
        return success(gpsLatestService.getLatestByVin(vin));
    }

    /**
     * 灌入轨迹点到 ClickHouse 缓冲（需 tw.telemetry.clickhouse.enabled=true）
     */
    @PostMapping("track/ingest")
    public Result<Integer> trackIngest(@RequestBody List<TwGpsTrackPointDto> points) {
        return success(gpsTrackService.ingest(points));
    }

    /**
     * 立即刷盘轨迹缓冲到 ClickHouse
     */
    @PostMapping("track/flush")
    public Result<Boolean> trackFlush() {
        gpsTrackService.flush();
        return success(true);
    }
}
