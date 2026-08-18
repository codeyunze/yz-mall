-- 第三方开放客户端备案与授权 DDL（mall-sys 库）
-- 逻辑删除：invalid=0 有效；删除时 MyBatis-Plus delval=current_timestamp

CREATE TABLE IF NOT EXISTS `sys_open_client` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `client_id` varchar(64) NOT NULL COMMENT '客户端标识，对外唯一',
  `client_name` varchar(128) NOT NULL COMMENT '应用名称',
  `contact_name` varchar(64) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(32) DEFAULT NULL COMMENT '联系电话',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `expire_time` datetime DEFAULT NULL COMMENT '授权到期时间，空表示长期',
  `ip_whitelist` varchar(512) DEFAULT NULL COMMENT 'IP白名单，逗号分隔，空不限制',
  `rate_limit_qps` int DEFAULT NULL COMMENT '可选QPS上限',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_id` bigint DEFAULT NULL COMMENT '创建人用户ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_client_id_invalid` (`client_id`, `invalid`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方开放客户端';

CREATE TABLE IF NOT EXISTS `sys_open_client_key` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `client_id` varchar(64) NOT NULL COMMENT '客户端标识',
  `key_version` int NOT NULL DEFAULT 1 COMMENT '密钥版本号',
  `client_public_key` text NOT NULL COMMENT '客户端SM2公钥Base64/PEM',
  `key_status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0停用 1当前生效',
  `effect_time` datetime NOT NULL COMMENT '生效时间',
  `expire_time` datetime DEFAULT NULL COMMENT '失效时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_id` bigint DEFAULT NULL COMMENT '创建人用户ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  KEY `idx_client_status` (`client_id`, `key_status`),
  UNIQUE KEY `uk_client_version_invalid` (`client_id`, `key_version`, `invalid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方客户端SM2公钥';

CREATE TABLE IF NOT EXISTS `sys_open_client_auth` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `client_id` varchar(64) NOT NULL COMMENT '客户端标识',
  `permission_code` varchar(128) NOT NULL COMMENT '开放API权限码，如open:tw:vehicle:query',
  `auth_status` tinyint NOT NULL DEFAULT 1 COMMENT '授权状态：0撤销 1有效',
  `grant_time` datetime NOT NULL COMMENT '授权时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间，空长期',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_id` bigint DEFAULT NULL COMMENT '创建人用户ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_client_perm_invalid` (`client_id`, `permission_code`, `invalid`),
  KEY `idx_client_status` (`client_id`, `auth_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方客户端接口授权';
