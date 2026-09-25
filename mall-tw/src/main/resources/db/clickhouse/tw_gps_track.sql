-- Titan Watch GPS 轨迹明细（ClickHouse）
-- 写入：消费侧批量 INSERT；查询：vin + gps_time 范围
-- 坐标系：WGS84；TTL 演示可调短

CREATE DATABASE IF NOT EXISTS tw;

CREATE TABLE IF NOT EXISTS tw.tw_gps_track
(
    vin String,
    vehicle_id UInt64 DEFAULT 0,
    lng Float64,
    lat Float64,
    altitude Nullable(Float64),
    speed Nullable(Float64),
    heading Nullable(Float64),
    gps_time DateTime64(3, 'Asia/Shanghai'),
    receive_time DateTime64(3, 'Asia/Shanghai'),
    soc Nullable(Float32),
    signal_level Nullable(Int8)
)
ENGINE = MergeTree
PARTITION BY toYYYYMM(gps_time)
ORDER BY (vin, gps_time)
TTL gps_time + INTERVAL 90 DAY
SETTINGS index_granularity = 8192;
