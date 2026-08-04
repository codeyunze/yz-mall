package com.yz.mall.tw.vehicle.constant;

/**
 * 车辆模块常量
 */
public final class TwVehicleConstants {

    private TwVehicleConstants() {
    }

    /** 启用 */
    public static final int STATUS_ENABLED = 1;
    /** 停用 */
    public static final int STATUS_DISABLED = 0;

    /** 绑定中 */
    public static final int BIND_STATUS_ACTIVE = 1;
    /** 已解绑 */
    public static final int BIND_STATUS_UNBOUND = 0;

    /** 运营绑定 */
    public static final int BIND_SOURCE_OPERATOR = 1;
    /** 过户 */
    public static final int BIND_SOURCE_TRANSFER = 2;

    /** 授权有效 */
    public static final int AUTH_STATUS_ACTIVE = 1;
    /** 授权撤销 */
    public static final int AUTH_STATUS_REVOKED = 0;

    /** 关系：无 */
    public static final int RELATION_NONE = 0;
    /** 关系：车主 */
    public static final int RELATION_OWNER = 1;
    /** 关系：授权用户 */
    public static final int RELATION_AUTH = 2;

    /** 查看档案 */
    public static final int SCOPE_VIEW = 1;
    /** 位置轨迹 */
    public static final int SCOPE_LOCATION = 2;
    /** 远程控车 */
    public static final int SCOPE_COMMAND = 4;
    /** 默认授权：查看+位置 */
    public static final int SCOPE_DEFAULT = SCOPE_VIEW | SCOPE_LOCATION;
    /** 车主全权限 */
    public static final int SCOPE_ALL = SCOPE_VIEW | SCOPE_LOCATION | SCOPE_COMMAND;

    /** Redis 在线状态 key 前缀 */
    public static final String REDIS_ONLINE_PREFIX = "tw:online:";
    /** Redis 最新 GPS key 前缀 */
    public static final String REDIS_GPS_PREFIX = "tw:gps:latest:";
}
