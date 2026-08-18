-- 车系 / 车型主数据 DDL（P0）
-- 逻辑删除：invalid=0 有效；删除时 invalid 置为当前行 id

CREATE TABLE IF NOT EXISTS `tw_vehicle_series` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `series_code` varchar(64) NOT NULL COMMENT '车系编码，业务唯一',
  `series_name` varchar(64) NOT NULL COMMENT '车系名称',
  `brand_name` varchar(64) DEFAULT NULL COMMENT '品牌名称（展示用）',
  `cover_file_id` bigint DEFAULT NULL COMMENT '车系封面图文件ID',
  `sort_no` int NOT NULL DEFAULT 0 COMMENT '排序号，越小越靠前',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '启用状态：0停用 1启用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_id` bigint DEFAULT NULL COMMENT '创建人用户ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_series_code_invalid` (`series_code`, `invalid`),
  KEY `idx_status_sort` (`status`, `sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆车系';

CREATE TABLE IF NOT EXISTS `tw_vehicle_model` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `series_id` bigint NOT NULL COMMENT '车系ID，关联tw_vehicle_series.id',
  `series_code` varchar(64) NOT NULL COMMENT '车系编码冗余',
  `model_code` varchar(64) NOT NULL COMMENT '车型编码，业务唯一，供tw_vehicle引用',
  `model_name` varchar(64) NOT NULL COMMENT '车型名称',
  `energy_type` tinyint DEFAULT NULL COMMENT '能源类型：1纯电 2插混 3增程 4燃油(预留) 9其他',
  `drive_type` tinyint DEFAULT NULL COMMENT '驱动：1两驱 2四驱 9其他',
  `seat_count` tinyint DEFAULT NULL COMMENT '座位数',
  `battery_kwh` decimal(8,2) DEFAULT NULL COMMENT '电池容量kWh',
  `range_km` int DEFAULT NULL COMMENT '工况续航km（展示用）',
  `cover_file_id` bigint DEFAULT NULL COMMENT '车型封面图文件ID',
  `sort_no` int NOT NULL DEFAULT 0 COMMENT '同车系内排序号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '启用状态：0停用 1启用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_id` bigint DEFAULT NULL COMMENT '创建人用户ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_model_code_invalid` (`model_code`, `invalid`),
  KEY `idx_series_status` (`series_id`, `status`, `sort_no`),
  KEY `idx_series_code` (`series_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆车型';

-- 车辆档案衔接：冗余车系编码（已存在列则跳过）
-- ALTER TABLE `tw_vehicle`
--   ADD COLUMN `series_code` varchar(64) DEFAULT NULL COMMENT '车系编码冗余' AFTER `vin`,
--   ADD KEY `idx_model_code` (`model_code`),
--   ADD KEY `idx_series_code` (`series_code`);

-- 演示种子数据
INSERT INTO `tw_vehicle_series` (`id`, `series_code`, `series_name`, `brand_name`, `sort_no`, `status`, `invalid`)
VALUES
  (1001, 'HAN', '汉', '比亚迪', 10, 1, 0),
  (1002, 'SONG_PLUS', '宋PLUS', '比亚迪', 20, 1, 0)
ON DUPLICATE KEY UPDATE `series_name`=VALUES(`series_name`);

INSERT INTO `tw_vehicle_model` (`id`, `series_id`, `series_code`, `model_code`, `model_name`, `energy_type`, `drive_type`, `seat_count`, `battery_kwh`, `range_km`, `sort_no`, `status`, `invalid`)
VALUES
  (2001, 1001, 'HAN', 'HAN_EV_605_ZUN', '汉 EV 605KM 尊贵型', 1, 1, 5, 85.40, 605, 10, 1, 0),
  (2002, 1001, 'HAN', 'HAN_EV_715_QI', '汉 EV 715KM 旗舰型', 1, 2, 5, 85.40, 715, 20, 1, 0),
  (2003, 1002, 'SONG_PLUS', 'SONG_PLUS_DM_i_112', '宋PLUS DM-i 112KM', 2, 1, 5, 18.30, 112, 10, 1, 0),
  (2004, 1002, 'SONG_PLUS', 'SONG_PLUS_EV_605', '宋PLUS EV 605KM', 1, 1, 5, 87.00, 605, 20, 1, 0)
ON DUPLICATE KEY UPDATE `model_name`=VALUES(`model_name`);
