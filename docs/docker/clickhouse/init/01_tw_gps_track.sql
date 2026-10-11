-- Docker ClickHouse 初始化：挂载到 /docker-entrypoint-initdb.d/
-- 与 mall-tw/src/main/resources/db/clickhouse/tw_gps_track.sql 保持一致

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
