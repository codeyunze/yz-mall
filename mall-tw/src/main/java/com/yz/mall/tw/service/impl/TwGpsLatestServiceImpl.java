package com.yz.mall.tw.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.tw.constant.TwTelemetryConstants;
import com.yz.mall.tw.dto.TwGpsLatestWriteDto;
import com.yz.mall.tw.entity.TwGpsLatest;
import com.yz.mall.tw.mapper.TwGpsLatestMapper;
import com.yz.mall.tw.service.TwGpsLatestService;
import com.yz.mall.tw.vo.TwGpsLatestVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 最新 GPS：Redis 热读 + MySQL 每 VIN 一行 UPSERT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TwGpsLatestServiceImpl implements TwGpsLatestService {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
    private static final ObjectMapper OBJECT_MAPPER = JacksonUtil.getObjectMapper();

    private final TwGpsLatestMapper gpsLatestMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TwGpsLatestVo saveLatest(TwGpsLatestWriteDto dto) {
        if (dto == null || StrUtil.isBlank(dto.getVin())) {
            log.warn("GPS 写入丢弃：VIN 为空");
            return null;
        }
        if (!isValidCoordinate(dto.getLng(), dto.getLat())) {
            log.warn("GPS 写入丢弃：非法坐标 vin={} lng={} lat={}", dto.getVin(), dto.getLng(), dto.getLat());
            return null;
        }
        String vin = dto.getVin().trim().toUpperCase();
        LocalDateTime receiveTime = LocalDateTime.now();
        LocalDateTime gpsTime = dto.getGpsTime() != null ? dto.getGpsTime() : receiveTime;

        TwGpsLatest existing = gpsLatestMapper.selectOne(Wrappers.<TwGpsLatest>lambdaQuery().eq(TwGpsLatest::getVin, vin).last("LIMIT 1"));
        TwGpsLatest entity;
        if (existing == null) {
            entity = new TwGpsLatest();
            entity.setId(IdUtil.getSnowflakeNextId());
            entity.setVin(vin);
            entity.setCreateTime(receiveTime);
            applyWriteFields(entity, dto, gpsTime, receiveTime);
            gpsLatestMapper.insert(entity);
        } else {
            // 乱序保护：仅当新点不早于库内 gps_time 时覆盖
            if (existing.getGpsTime() != null && gpsTime.isBefore(existing.getGpsTime())) {
                log.debug("GPS 乱序跳过 MySQL：vin={} new={} old={}", vin, gpsTime, existing.getGpsTime());
                TwGpsLatestVo cached = toVo(existing);
                writeRedis(cached);
                return cached;
            }
            entity = existing;
            applyWriteFields(entity, dto, gpsTime, receiveTime);
            entity.setUpdateTime(receiveTime);
            gpsLatestMapper.updateById(entity);
        }
        TwGpsLatestVo vo = toVo(entity);
        writeRedis(vo);
        return vo;
    }

    @Override
    public TwGpsLatestVo getLatestByVin(String vin) {
        if (StrUtil.isBlank(vin)) {
            return null;
        }
        String normalizedVin = vin.trim().toUpperCase();
        String key = redisKey(normalizedVin);
        String raw = stringRedisTemplate.opsForValue().get(key);
        TwGpsLatestVo fromRedis = parseRedis(raw);
        if (fromRedis != null) {
            if (StrUtil.isBlank(fromRedis.getVin())) {
                fromRedis.setVin(normalizedVin);
            }
            return fromRedis;
        }
        TwGpsLatest entity = gpsLatestMapper.selectOne(Wrappers.<TwGpsLatest>lambdaQuery().eq(TwGpsLatest::getVin, normalizedVin).last("LIMIT 1"));
        if (entity == null) {
            return null;
        }
        TwGpsLatestVo vo = toVo(entity);
        writeRedis(vo);
        return vo;
    }

    @Override
    public List<TwGpsLatestVo> listLatestByVins(Collection<String> vins) {
        if (vins == null || vins.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> distinct = vins.stream().filter(StrUtil::isNotBlank).map(v -> v.trim().toUpperCase()).distinct().collect(Collectors.toList());
        if (distinct.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> keys = distinct.stream().map(this::redisKey).collect(Collectors.toList());
        List<String> rawList = stringRedisTemplate.opsForValue().multiGet(keys);
        Map<String, TwGpsLatestVo> hit = new LinkedHashMap<>();
        List<String> miss = new ArrayList<>();
        for (int i = 0; i < distinct.size(); i++) {
            String vin = distinct.get(i);
            String raw = rawList != null && i < rawList.size() ? rawList.get(i) : null;
            TwGpsLatestVo vo = parseRedis(raw);
            if (vo != null) {
                hit.put(vin, vo);
            } else {
                miss.add(vin);
            }
        }
        if (!miss.isEmpty()) {
            List<TwGpsLatest> rows = gpsLatestMapper.selectList(Wrappers.<TwGpsLatest>lambdaQuery().in(TwGpsLatest::getVin, miss));
            for (TwGpsLatest row : rows) {
                TwGpsLatestVo vo = toVo(row);
                writeRedis(vo);
                hit.put(row.getVin(), vo);
            }
        }
        return new ArrayList<>(hit.values());
    }

    private void applyWriteFields(TwGpsLatest entity, TwGpsLatestWriteDto dto, LocalDateTime gpsTime, LocalDateTime receiveTime) {
        entity.setVehicleId(dto.getVehicleId());
        entity.setLng(dto.getLng());
        entity.setLat(dto.getLat());
        entity.setAltitude(dto.getAltitude());
        entity.setSpeed(dto.getSpeed());
        entity.setHeading(dto.getHeading());
        entity.setGpsTime(gpsTime);
        entity.setReceiveTime(receiveTime);
        entity.setSoc(dto.getSoc());
        entity.setSignalLevel(dto.getSignalLevel());
        entity.setRawExt(dto.getRawExt());
    }

    private void writeRedis(TwGpsLatestVo vo) {
        if (vo == null || StrUtil.isBlank(vo.getVin())) {
            return;
        }
        Map<String, Object> snap = new LinkedHashMap<>();
        snap.put("vin", vo.getVin());
        snap.put("vehicleId", vo.getVehicleId());
        snap.put("lng", vo.getLng());
        snap.put("lat", vo.getLat());
        snap.put("altitude", vo.getAltitude());
        snap.put("speed", vo.getSpeed());
        snap.put("heading", vo.getHeading());
        String gpsTimeStr = vo.getGpsTime() != null ? vo.getGpsTime().format(DT_FMT) : null;
        snap.put("gpsTime", gpsTimeStr);
        snap.put("receiveTime", vo.getReceiveTime() != null ? vo.getReceiveTime().format(DT_FMT) : null);
        // 兼容 TwVehicleRealtimeSupport：reportTime = gpsTime
        snap.put("reportTime", gpsTimeStr);
        snap.put("soc", vo.getSoc());
        snap.put("signalLevel", vo.getSignalLevel());
        try {
            stringRedisTemplate.opsForValue().set(redisKey(vo.getVin()), OBJECT_MAPPER.writeValueAsString(snap), TwTelemetryConstants.REDIS_GPS_LATEST_TTL_DAYS, TimeUnit.DAYS);
        } catch (Exception ex) {
            log.warn("写入 Redis GPS 失败 vin={}: {}", vo.getVin(), ex.getMessage());
        }
    }

    private TwGpsLatestVo parseRedis(String raw) {
        if (StrUtil.isBlank(raw)) {
            return null;
        }
        try {
            JsonNode json = OBJECT_MAPPER.readTree(raw);
            TwGpsLatestVo vo = new TwGpsLatestVo();
            vo.setVin(textOrNull(json, "vin"));
            if (json.hasNonNull("vehicleId")) {
                vo.setVehicleId(json.get("vehicleId").asLong());
            }
            vo.setLng(toDecimal(json, "lng"));
            vo.setLat(toDecimal(json, "lat"));
            vo.setAltitude(toDecimal(json, "altitude"));
            vo.setSpeed(toDecimal(json, "speed"));
            vo.setHeading(toDecimal(json, "heading"));
            String gpsTime = textOrNull(json, "gpsTime");
            if (StrUtil.isBlank(gpsTime)) {
                gpsTime = textOrNull(json, "reportTime");
            }
            if (StrUtil.isNotBlank(gpsTime)) {
                vo.setGpsTime(LocalDateTime.parse(gpsTime, DT_FMT));
            }
            String receiveTime = textOrNull(json, "receiveTime");
            if (StrUtil.isNotBlank(receiveTime)) {
                vo.setReceiveTime(LocalDateTime.parse(receiveTime, DT_FMT));
            }
            vo.setSoc(toDecimal(json, "soc"));
            if (json.hasNonNull("signalLevel")) {
                vo.setSignalLevel(json.get("signalLevel").asInt());
            }
            return vo;
        } catch (Exception ex) {
            log.warn("解析 Redis GPS 失败: {}", ex.getMessage());
            return null;
        }
    }

    private static String textOrNull(JsonNode json, String key) {
        JsonNode node = json.get(key);
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText();
    }

    private static BigDecimal toDecimal(JsonNode json, String key) {
        JsonNode node = json.get(key);
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isNumber()) {
            return node.decimalValue();
        }
        String text = node.asText();
        if (StrUtil.isBlank(text)) {
            return null;
        }
        return new BigDecimal(text);
    }

    private TwGpsLatestVo toVo(TwGpsLatest entity) {
        TwGpsLatestVo vo = new TwGpsLatestVo();
        vo.setVin(entity.getVin());
        vo.setVehicleId(entity.getVehicleId());
        vo.setLng(entity.getLng());
        vo.setLat(entity.getLat());
        vo.setAltitude(entity.getAltitude());
        vo.setSpeed(entity.getSpeed());
        vo.setHeading(entity.getHeading());
        vo.setGpsTime(entity.getGpsTime());
        vo.setReceiveTime(entity.getReceiveTime());
        vo.setSoc(entity.getSoc());
        vo.setSignalLevel(entity.getSignalLevel());
        return vo;
    }

    private String redisKey(String vin) {
        return TwTelemetryConstants.REDIS_GPS_LATEST_PREFIX + vin;
    }

    /**
     * WGS84 合法范围；拒绝 NaN / Infinity。
     */
    static boolean isValidCoordinate(BigDecimal lng, BigDecimal lat) {
        if (lng == null || lat == null) {
            return false;
        }
        double lo = lng.doubleValue();
        double la = lat.doubleValue();
        if (Double.isNaN(lo) || Double.isNaN(la) || Double.isInfinite(lo) || Double.isInfinite(la)) {
            return false;
        }
        return lo >= -180D && lo <= 180D && la >= -90D && la <= 90D;
    }
}
