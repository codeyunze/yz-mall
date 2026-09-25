-- Titan Watch 车辆最新 GPS 位置（遥测 latest 兜底）
-- 每 VIN 一行 UPSERT；行数≈车辆数。轨迹明细见 ClickHouse tw_gps_track。

CREATE TABLE IF NOT EXISTS `tw_gps_latest` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `vin` varchar(32) NOT NULL COMMENT '车架号VIN',
  `vehicle_id` bigint DEFAULT NULL COMMENT '车辆ID冗余，可空',
  `lng` decimal(10,7) NOT NULL COMMENT '经度（WGS84）',
  `lat` decimal(10,7) NOT NULL COMMENT '纬度（WGS84）',
  `altitude` decimal(10,2) DEFAULT NULL COMMENT '海拔米',
  `speed` decimal(8,2) DEFAULT NULL COMMENT '速度km/h',
  `heading` decimal(6,2) DEFAULT NULL COMMENT '航向角0-360',
  `gps_time` datetime NOT NULL COMMENT 'GPS定位时间（车机侧）',
  `receive_time` datetime NOT NULL COMMENT '云端接收时间',
  `soc` decimal(5,2) DEFAULT NULL COMMENT '电池SOC百分比（P1）',
  `signal_level` tinyint DEFAULT NULL COMMENT '信号强度等级（P1）',
  `raw_ext` json DEFAULT NULL COMMENT '扩展字段JSON',
  `create_id` bigint DEFAULT NULL COMMENT '创建人用户ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vin` (`vin`),
  KEY `idx_gps_time` (`gps_time`),
  KEY `idx_vehicle_id` (`vehicle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆最新GPS位置';
