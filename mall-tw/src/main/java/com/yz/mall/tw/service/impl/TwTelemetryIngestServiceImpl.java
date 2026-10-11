package com.yz.mall.tw.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.tw.config.TwTelemetryProperties;
import com.yz.mall.tw.dto.TwGpsLatestWriteDto;
import com.yz.mall.tw.dto.TwGpsTrackPointDto;
import com.yz.mall.tw.dto.TwTelemetryRawMessage;
import com.yz.mall.tw.service.TwGpsLatestService;
import com.yz.mall.tw.service.TwTelemetryIngestService;
import com.yz.mall.tw.support.TwGpsSpeedDenoise;
import com.yz.mall.tw.support.TwGpsTrackBuffer;
import com.yz.mall.tw.vo.TwGpsLatestVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 三写闭环：先 Latest（Redis+MySQL），再可选入 CH 缓冲；CH 异常不影响 latest。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TwTelemetryIngestServiceImpl implements TwTelemetryIngestService {

    private static final DateTimeFormatter NORM_DT = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
    private static final ObjectMapper OBJECT_MAPPER = JacksonUtil.getObjectMapper();

    private final TwGpsLatestService gpsLatestService;
    private final TwTelemetryProperties properties;
    private final ObjectProvider<TwGpsTrackBuffer> trackBufferProvider;

    /**
     * 进程内上一合法点（速度去噪用；重启后清空可接受）
     */
    private final Map<String, LastPoint> lastAccepted = new ConcurrentHashMap<>();

    @Override
    public boolean handleRaw(String payload, String kafkaKey) {
        if (StrUtil.isBlank(payload)) {
            log.warn("遥测消息丢弃：payload 为空");
            return false;
        }
        TwTelemetryRawMessage msg;
        try {
            msg = OBJECT_MAPPER.readValue(payload, TwTelemetryRawMessage.class);
        } catch (Exception ex) {
            log.warn("遥测消息丢弃：JSON 解析失败: {}", ex.getMessage());
            return false;
        }
        if (msg == null) {
            return false;
        }
        if (StrUtil.isBlank(msg.getVin()) && StrUtil.isNotBlank(kafkaKey)) {
            msg.setVin(kafkaKey.trim());
        }
        if (StrUtil.isBlank(msg.getVin())) {
            log.warn("遥测消息丢弃：VIN 为空");
            return false;
        }
        String vin = msg.getVin().trim().toUpperCase();
        if (!TwGpsLatestServiceImpl.isValidCoordinate(msg.getLng(), msg.getLat())) {
            log.warn("遥测消息丢弃：非法坐标 vin={} lng={} lat={}", vin, msg.getLng(), msg.getLat());
            return false;
        }
        LocalDateTime gpsTime = parseGpsTime(msg.getGpsTime());
        if (gpsTime == null) {
            gpsTime = LocalDateTime.now();
        }

        TwTelemetryProperties.Kafka kafka = properties.getKafka();
        if (kafka.isSpeedJumpFilter()) {
            LastPoint prev = lastAccepted.get(vin);
            if (prev != null && TwGpsSpeedDenoise.shouldDrop(prev.lng, prev.lat, prev.gpsTime, msg.getLng(), msg.getLat(), gpsTime, kafka.getMaxSpeedKmh())) {
                log.warn("遥测消息丢弃：速度跳变 vin={} gpsTime={}", vin, gpsTime);
                return false;
            }
        }

        TwGpsLatestWriteDto writeDto = new TwGpsLatestWriteDto();
        writeDto.setVin(vin);
        writeDto.setVehicleId(msg.getVehicleId());
        writeDto.setLng(msg.getLng());
        writeDto.setLat(msg.getLat());
        writeDto.setAltitude(msg.getAltitude());
        writeDto.setSpeed(msg.getSpeed());
        writeDto.setHeading(msg.getHeading());
        writeDto.setGpsTime(gpsTime);
        writeDto.setSoc(msg.getSoc());
        writeDto.setSignalLevel(msg.getSignalLevel());

        TwGpsLatestVo saved = gpsLatestService.saveLatest(writeDto);
        if (saved == null) {
            return false;
        }
        lastAccepted.put(vin, new LastPoint(msg.getLng(), msg.getLat(), gpsTime));

        // CH 失败不影响 latest：缓冲内部吞异常；未启用则跳过
        TwGpsTrackBuffer buffer = trackBufferProvider.getIfAvailable();
        if (buffer != null) {
            TwGpsTrackPointDto point = new TwGpsTrackPointDto();
            point.setVin(vin);
            point.setVehicleId(msg.getVehicleId());
            point.setLng(msg.getLng());
            point.setLat(msg.getLat());
            point.setAltitude(msg.getAltitude());
            point.setSpeed(msg.getSpeed());
            point.setHeading(msg.getHeading());
            point.setGpsTime(gpsTime);
            point.setReceiveTime(LocalDateTime.now());
            point.setSoc(msg.getSoc());
            point.setSignalLevel(msg.getSignalLevel());
            try {
                buffer.offer(point);
            } catch (Exception ex) {
                log.error("轨迹缓冲入队失败 vin={}（latest 已写入）: {}", vin, ex.getMessage());
            }
        }
        return true;
    }

    /**
     * 解析 GPS 时间：ISO-8601（含偏移）或 yyyy-MM-dd HH:mm:ss。
     */
    static LocalDateTime parseGpsTime(String raw) {
        if (StrUtil.isBlank(raw)) {
            return null;
        }
        String s = raw.trim();
        try {
            if (s.contains("T")) {
                try {
                    return OffsetDateTime.parse(s).toLocalDateTime();
                } catch (DateTimeParseException ignored) {
                    return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            }
            if (s.length() >= 19) {
                return LocalDateTime.parse(s.substring(0, 19), NORM_DT);
            }
            return LocalDateTime.parse(s);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static final class LastPoint {
        private final BigDecimal lng;
        private final BigDecimal lat;
        private final LocalDateTime gpsTime;

        private LastPoint(BigDecimal lng, BigDecimal lat, LocalDateTime gpsTime) {
            this.lng = lng;
            this.lat = lat;
            this.gpsTime = gpsTime;
        }
    }
}
