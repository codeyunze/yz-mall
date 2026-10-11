package com.yz.mall.tw.service.impl;

import cn.hutool.core.util.StrUtil;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.tw.config.TwTelemetryProperties;
import com.yz.mall.tw.dto.TwGpsTrackPointDto;
import com.yz.mall.tw.dto.TwGpsTrackQueryDto;
import com.yz.mall.tw.entity.TwVehicle;
import com.yz.mall.tw.service.TwGpsTrackService;
import com.yz.mall.tw.service.TwVehicleService;
import com.yz.mall.tw.support.TwClickHouseHttpClient;
import com.yz.mall.tw.support.TwGpsTrackBuffer;
import com.yz.mall.tw.support.TwGpsTrackSampler;
import com.yz.mall.tw.support.TwTelemetryAccessSupport;
import com.yz.mall.tw.vo.TwGpsTrackPointVo;
import com.yz.mall.tw.vo.TwGpsTrackVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 轨迹：缓冲写 CH + 时间窗查询抽稀
 */
@Service
@RequiredArgsConstructor
public class TwGpsTrackServiceImpl implements TwGpsTrackService {

    private final TwVehicleService vehicleService;
    private final TwTelemetryAccessSupport accessSupport;
    private final TwTelemetryProperties properties;
    private final ObjectProvider<TwGpsTrackBuffer> trackBufferProvider;
    private final ObjectProvider<TwClickHouseHttpClient> clickHouseClientProvider;

    @Override
    public int ingest(List<TwGpsTrackPointDto> points) {
        TwGpsTrackBuffer buffer = trackBufferProvider.getIfAvailable();
        if (buffer == null) {
            throw new BusinessException("轨迹存储未启用，请配置 tw.telemetry.clickhouse.enabled=true");
        }
        if (points == null || points.isEmpty()) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        List<TwGpsTrackPointDto> accepted = new ArrayList<>();
        for (TwGpsTrackPointDto p : points) {
            if (p == null || StrUtil.isBlank(p.getVin()) || p.getLng() == null || p.getLat() == null || p.getGpsTime() == null) {
                continue;
            }
            if (!TwGpsLatestServiceImpl.isValidCoordinate(p.getLng(), p.getLat())) {
                continue;
            }
            TwGpsTrackPointDto copy = new TwGpsTrackPointDto();
            copy.setVin(p.getVin().trim().toUpperCase());
            copy.setVehicleId(p.getVehicleId());
            copy.setLng(p.getLng());
            copy.setLat(p.getLat());
            copy.setAltitude(p.getAltitude());
            copy.setSpeed(p.getSpeed());
            copy.setHeading(p.getHeading());
            copy.setGpsTime(p.getGpsTime());
            copy.setReceiveTime(p.getReceiveTime() != null ? p.getReceiveTime() : now);
            copy.setSoc(p.getSoc());
            copy.setSignalLevel(p.getSignalLevel());
            accepted.add(copy);
        }
        buffer.offerAll(accepted);
        return accepted.size();
    }

    @Override
    public void flush() {
        TwGpsTrackBuffer buffer = trackBufferProvider.getIfAvailable();
        if (buffer == null) {
            throw new BusinessException("轨迹存储未启用，请配置 tw.telemetry.clickhouse.enabled=true");
        }
        buffer.flush();
    }

    @Override
    public TwGpsTrackVo queryTrack(TwGpsTrackQueryDto dto) {
        TwClickHouseHttpClient client = clickHouseClientProvider.getIfAvailable();
        if (client == null) {
            throw new BusinessException("轨迹存储未启用，请配置 tw.telemetry.clickhouse.enabled=true");
        }
        if (dto == null || dto.getStartTime() == null || dto.getEndTime() == null) {
            throw new BusinessException("请指定查询时间范围");
        }
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BusinessException("结束时间不能早于开始时间");
        }
        int maxHours = Math.max(1, properties.getTrack().getMaxQueryHours());
        // 允许恰好 maxHours（如 24h）；超出则拒绝
        if (Duration.between(dto.getStartTime(), dto.getEndTime()).toMinutes() > maxHours * 60L) {
            throw new BusinessException("查询时间范围不能超过 " + maxHours + " 小时");
        }
        String vin = resolveVin(dto.getVin(), dto.getVehicleId());
        accessSupport.assertLocationAccess(vin, dto.getVehicleId());

        List<TwGpsTrackPointDto> rows = client.queryByVinAndTime(vin, dto.getStartTime(), dto.getEndTime());
        List<TwGpsTrackPointVo> points = new ArrayList<>(rows.size());
        for (TwGpsTrackPointDto row : rows) {
            TwGpsTrackPointVo vo = new TwGpsTrackPointVo();
            vo.setLng(row.getLng());
            vo.setLat(row.getLat());
            vo.setSpeed(row.getSpeed());
            vo.setHeading(row.getHeading());
            vo.setGpsTime(row.getGpsTime());
            points.add(vo);
        }
        int maxPoints = resolveMaxPoints(dto.getMaxPoints());
        TwGpsTrackVo result = new TwGpsTrackVo();
        result.setVin(vin);
        result.setTotal(points.size());
        if (points.size() > maxPoints) {
            result.setPoints(TwGpsTrackSampler.sample(points, maxPoints));
            result.setSampled(true);
        } else {
            result.setPoints(points);
            result.setSampled(false);
        }
        return result;
    }

    private int resolveMaxPoints(Integer maxPoints) {
        TwTelemetryProperties.Track track = properties.getTrack();
        int absolute = Math.max(1, track.getAbsoluteMaxPoints());
        int def = Math.min(Math.max(1, track.getDefaultMaxPoints()), absolute);
        if (maxPoints == null || maxPoints <= 0) {
            return def;
        }
        return Math.min(maxPoints, absolute);
    }

    private String resolveVin(String vin, Long vehicleId) {
        if (vehicleId != null) {
            TwVehicle vehicle = vehicleService.getById(vehicleId);
            if (vehicle == null) {
                throw new BusinessException("车辆不存在");
            }
            if (StrUtil.isNotBlank(vin) && !Objects.equals(vehicle.getVin(), vin.trim().toUpperCase())) {
                throw new BusinessException("VIN 与车辆ID不匹配");
            }
            return vehicle.getVin();
        }
        if (StrUtil.isBlank(vin)) {
            throw new BusinessException("请指定 VIN 或车辆ID");
        }
        return vin.trim().toUpperCase();
    }
}
