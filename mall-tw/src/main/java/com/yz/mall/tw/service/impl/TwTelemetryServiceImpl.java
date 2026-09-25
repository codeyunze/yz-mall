package com.yz.mall.tw.service.impl;

import cn.hutool.core.util.StrUtil;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.tw.constant.TwTelemetryConstants;
import com.yz.mall.tw.dto.TwGpsLatestBatchQueryDto;
import com.yz.mall.tw.entity.TwVehicle;
import com.yz.mall.tw.service.TwGpsLatestService;
import com.yz.mall.tw.service.TwTelemetryService;
import com.yz.mall.tw.service.TwVehicleService;
import com.yz.mall.tw.support.TwTelemetryAccessSupport;
import com.yz.mall.tw.support.TwVehicleRealtimeSupport;
import com.yz.mall.tw.vo.TwGpsLatestVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 遥测查询：access/check + Latest 读 + 在线标记
 */
@Service
@RequiredArgsConstructor
public class TwTelemetryServiceImpl implements TwTelemetryService {

    private final TwGpsLatestService gpsLatestService;
    private final TwVehicleService vehicleService;
    private final TwTelemetryAccessSupport accessSupport;
    private final TwVehicleRealtimeSupport realtimeSupport;

    @Override
    public TwGpsLatestVo getLatest(String vin, Long vehicleId) {
        ResolvedVehicle resolved = resolveVehicle(vin, vehicleId);
        accessSupport.assertLocationAccess(resolved.vin(), resolved.vehicleId());
        TwGpsLatestVo vo = gpsLatestService.getLatestByVin(resolved.vin());
        fillOnline(vo, resolved.vin());
        return vo;
    }

    @Override
    public List<TwGpsLatestVo> batchLatest(TwGpsLatestBatchQueryDto dto) {
        if (!accessSupport.isFleetOperator()) {
            throw new BusinessException("无权批量查询车辆位置");
        }
        if (dto == null || dto.getVins() == null || dto.getVins().isEmpty()) {
            return Collections.emptyList();
        }
        List<String> vins = dto.getVins().stream().filter(StrUtil::isNotBlank).map(v -> v.trim().toUpperCase()).distinct().collect(Collectors.toList());
        if (vins.size() > TwTelemetryConstants.LATEST_BATCH_MAX) {
            throw new BusinessException("批量查询 VIN 数量不能超过 " + TwTelemetryConstants.LATEST_BATCH_MAX);
        }
        if (vins.isEmpty()) {
            return Collections.emptyList();
        }
        List<TwGpsLatestVo> list = gpsLatestService.listLatestByVins(vins);
        Map<String, Boolean> onlineMap = realtimeSupport.batchOnline(vins);
        for (TwGpsLatestVo vo : list) {
            if (vo != null && StrUtil.isNotBlank(vo.getVin())) {
                vo.setOnline(onlineMap.getOrDefault(vo.getVin(), false));
            }
        }
        return list;
    }

    @Override
    public TwGpsLatestVo getLatestForExtend(String vin) {
        if (StrUtil.isBlank(vin)) {
            return null;
        }
        String normalized = vin.trim().toUpperCase();
        TwGpsLatestVo vo = gpsLatestService.getLatestByVin(normalized);
        fillOnline(vo, normalized);
        return vo;
    }

    private void fillOnline(TwGpsLatestVo vo, String vin) {
        if (vo == null) {
            return;
        }
        vo.setOnline(realtimeSupport.isOnline(vin));
    }

    private ResolvedVehicle resolveVehicle(String vin, Long vehicleId) {
        if (vehicleId != null) {
            TwVehicle vehicle = vehicleService.getById(vehicleId);
            if (vehicle == null) {
                throw new BusinessException("车辆不存在");
            }
            if (StrUtil.isNotBlank(vin) && !Objects.equals(vehicle.getVin(), vin.trim().toUpperCase())) {
                throw new BusinessException("VIN 与车辆ID不匹配");
            }
            return new ResolvedVehicle(vehicle.getVin(), vehicle.getId());
        }
        if (StrUtil.isBlank(vin)) {
            throw new BusinessException("请指定 VIN 或车辆ID");
        }
        return new ResolvedVehicle(vin.trim().toUpperCase(), null);
    }

    private static final class ResolvedVehicle {
        private final String vin;
        private final Long vehicleId;

        private ResolvedVehicle(String vin, Long vehicleId) {
            this.vin = vin;
            this.vehicleId = vehicleId;
        }

        private String vin() {
            return vin;
        }

        private Long vehicleId() {
            return vehicleId;
        }
    }
}
