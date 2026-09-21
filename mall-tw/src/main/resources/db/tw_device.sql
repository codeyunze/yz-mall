-- Titan Watch 终端管理 DDL（P0）

CREATE TABLE IF NOT EXISTS `tw_device` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `device_id` varchar(64) NOT NULL COMMENT '终端客户端ID，MQTT clientId/业务唯一键',
  `device_name` varchar(64) DEFAULT NULL COMMENT '终端名称/备注名',
  `device_type` varchar(32) DEFAULT NULL COMMENT '终端类型编码（字典 tw_device_type）',
  `secret_hash` varchar(128) NOT NULL COMMENT 'MQTT密码摘要，不明文存储',
  `secret_salt` varchar(64) DEFAULT NULL COMMENT '摘要盐值（算法需要时）',
  `secret_algo` varchar(32) NOT NULL DEFAULT 'BCRYPT' COMMENT '摘要算法：BCRYPT/HMAC_SHA256等',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `firmware_version` varchar(64) DEFAULT NULL COMMENT '固件版本（示意，OTA用）',
  `cert_sn` varchar(128) DEFAULT NULL COMMENT '证书序列号（P2预留）',
  `cert_expire_time` datetime DEFAULT NULL COMMENT '证书过期时间（P2预留）',
  `last_cred_reset_time` datetime DEFAULT NULL COMMENT '最近一次凭证重置时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_id` bigint DEFAULT NULL COMMENT '创建人用户ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id_invalid` (`device_id`, `invalid`),
  KEY `idx_status_ctime` (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='终端档案';

CREATE TABLE IF NOT EXISTS `tw_device_vehicle` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `device_pk` bigint NOT NULL COMMENT '终端主键，关联tw_device.id',
  `device_id` varchar(64) NOT NULL COMMENT '终端客户端ID冗余',
  `vehicle_id` bigint NOT NULL COMMENT '车辆ID，关联tw_vehicle.id',
  `vin` varchar(32) NOT NULL COMMENT 'VIN冗余，供鉴权ACL与排查',
  `bind_status` tinyint NOT NULL DEFAULT 1 COMMENT '绑定状态：0已解绑 1绑定中',
  `bind_time` datetime NOT NULL COMMENT '绑定时间',
  `unbind_time` datetime DEFAULT NULL COMMENT '解绑时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_id` bigint DEFAULT NULL COMMENT '创建人用户ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  KEY `idx_device_status` (`device_pk`, `bind_status`),
  KEY `idx_vehicle_status` (`vehicle_id`, `bind_status`),
  KEY `idx_device_id_status` (`device_id`, `bind_status`),
  KEY `idx_vin_status` (`vin`, `bind_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='终端车辆绑定';
