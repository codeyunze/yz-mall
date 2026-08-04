package com.yz.mall.tw.device.constant;

/**
 * 终端模块常量
 */
public final class TwDeviceConstants {

    private TwDeviceConstants() {
    }

    public static final int STATUS_ENABLED = 1;
    public static final int STATUS_DISABLED = 0;
    public static final int BIND_STATUS_ACTIVE = 1;
    public static final int BIND_STATUS_UNBOUND = 0;

    public static final String DEFAULT_DEVICE_TYPE = "SIMULATOR";
    public static final String SECRET_ALGO_BCRYPT = "BCRYPT";
    public static final String DEVICE_ID_PREFIX = "TW";

    public static final String REDIS_ONLINE_VIN_PREFIX = "tw:online:";
    public static final String REDIS_CRED_CACHE_PREFIX = "tw:device:cred:";
}
