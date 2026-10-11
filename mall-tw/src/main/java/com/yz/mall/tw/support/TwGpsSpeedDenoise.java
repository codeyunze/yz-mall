package com.yz.mall.tw.support;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 轻量速度跳变粗过滤（P0）
 */
public final class TwGpsSpeedDenoise {

    private TwGpsSpeedDenoise() {
    }

    /**
     * 根据相邻两点推算速度是否超过阈值。
     *
     * @param prevLng   上一点经度
     * @param prevLat   上一点纬度
     * @param prevTime  上一点时间
     * @param lng       当前经度
     * @param lat       当前纬度
     * @param gpsTime   当前时间
     * @param maxSpeedKmh 阈值 km/h
     * @return true 表示应丢弃（跳变过大）；时间间隔过小或缺失时不拦截
     */
    public static boolean shouldDrop(BigDecimal prevLng, BigDecimal prevLat, LocalDateTime prevTime,
                                     BigDecimal lng, BigDecimal lat, LocalDateTime gpsTime, double maxSpeedKmh) {
        if (prevLng == null || prevLat == null || prevTime == null || lng == null || lat == null || gpsTime == null) {
            return false;
        }
        long seconds = Duration.between(prevTime, gpsTime).getSeconds();
        if (seconds <= 0) {
            // 乱序或同秒：不按速度过滤（latest 侧另有乱序保护）
            return false;
        }
        double meters = haversineMeters(prevLat.doubleValue(), prevLng.doubleValue(), lat.doubleValue(), lng.doubleValue());
        double kmh = meters / seconds * 3.6D;
        return kmh > maxSpeedKmh;
    }

    /**
     * 球面距离（米）
     */
    static double haversineMeters(double lat1, double lng1, double lat2, double lng2) {
        double r = 6371000D;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }
}
