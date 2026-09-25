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

    /**
     * 批量最新位置 VIN 上限
     */
    public static final int LATEST_BATCH_MAX = 200;

    /**
     * 权限：单车最新位置
     */
    public static final String PERM_LATEST = "api:tw:telemetry:latest";

    /**
     * 权限：批量最新位置（运营/监控全量）
     */
    public static final String PERM_LATEST_BATCH = "api:tw:telemetry:latest:batch";
}
