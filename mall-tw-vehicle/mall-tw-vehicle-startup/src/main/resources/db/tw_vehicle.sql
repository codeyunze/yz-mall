-- Titan Watch 车辆档案 DDL（P0）
-- 逻辑删除：invalid=0 有效；删除时 invalid 置为当前行 id，以释放 VIN 再建档能力

CREATE TABLE IF NOT EXISTS `tw_vehicle` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `vin` varchar(32) NOT NULL COMMENT '车架号VIN，业务唯一键',
  `plate_no` varchar(16) DEFAULT NULL COMMENT '车牌号',
  `model_code` varchar(64) DEFAULT NULL COMMENT '车型编码（字典 tw_vehicle_model）',
  `model_name` varchar(64) DEFAULT NULL COMMENT '车型名称（冗余展示，可选）',
  `color` varchar(32) DEFAULT NULL COMMENT '车身颜色',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '启用状态：0停用 1启用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `cover_file_id` bigint DEFAULT NULL COMMENT '车辆封面图文件ID（mall-file）',
  `create_id` bigint DEFAULT NULL COMMENT '创建人用户ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vin_invalid` (`vin`, `invalid`),
  KEY `idx_plate_no` (`plate_no`),
  KEY `idx_status_ctime` (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆档案';

CREATE TABLE IF NOT EXISTS `tw_vehicle_owner` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `vehicle_id` bigint NOT NULL COMMENT '车辆ID，关联tw_vehicle.id',
  `vin` varchar(32) NOT NULL COMMENT 'VIN冗余，便于按VIN排查',
  `owner_user_id` bigint NOT NULL COMMENT '车主用户ID，关联sys用户',
  `bind_status` tinyint NOT NULL DEFAULT 1 COMMENT '绑定状态：0已解绑 1绑定中',
  `bind_time` datetime NOT NULL COMMENT '绑定时间',
  `unbind_time` datetime DEFAULT NULL COMMENT '解绑时间',
  `bind_source` tinyint NOT NULL DEFAULT 1 COMMENT '来源：1运营绑定 2过户 3自助(预留)',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_id` bigint DEFAULT NULL COMMENT '创建人用户ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  KEY `idx_vehicle_status` (`vehicle_id`, `bind_status`),
  KEY `idx_owner_status` (`owner_user_id`, `bind_status`),
  KEY `idx_vin` (`vin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆车主绑定';

CREATE TABLE IF NOT EXISTS `tw_vehicle_auth` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `vehicle_id` bigint NOT NULL COMMENT '车辆ID，关联tw_vehicle.id',
  `vin` varchar(32) NOT NULL COMMENT 'VIN冗余，便于按VIN排查',
  `owner_user_id` bigint NOT NULL COMMENT '授权时的车主用户ID',
  `auth_user_id` bigint NOT NULL COMMENT '被授权用户ID，关联sys用户',
  `auth_scope` int NOT NULL DEFAULT 3 COMMENT '授权范围位掩码：1查看 2位置轨迹 4远程控车，默认3=查看+位置',
  `auth_status` tinyint NOT NULL DEFAULT 1 COMMENT '授权状态：0已撤销 1有效',
  `grant_time` datetime NOT NULL COMMENT '授权时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间，空表示长期有效',
  `revoke_time` datetime DEFAULT NULL COMMENT '撤销时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_id` bigint DEFAULT NULL COMMENT '创建人用户ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  KEY `idx_vehicle_status` (`vehicle_id`, `auth_status`),
  KEY `idx_auth_user_status` (`auth_user_id`, `auth_status`),
  KEY `idx_vin` (`vin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆授权用户';
