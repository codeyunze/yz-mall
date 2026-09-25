package com.yz.mall.tw.constant;

/**
 * 遥测与轨迹常量
 */
public final class TwTelemetryConstants {

    private TwTelemetryConstants() {
    }

    /**
     * Redis 最新 GPS 快照 key 前缀（与 {@link TwVehicleConstants#REDIS_GPS_PREFIX} 一致）
     */
    public static final String REDIS_GPS_LATEST_PREFIX = TwVehicleConstants.REDIS_GPS_PREFIX;

    /**
     * Redis 最新点 TTL（天），滑动续期
     */
    public static final long REDIS_GPS_LATEST_TTL_DAYS = 7L;
}
