-- 租户 schema 增量迁移执行日志（主库 mall）
-- 逻辑删除：invalid=0 有效；删除时 MyBatis-Plus delval=current_timestamp
-- 查询：按 tenant_code + service_code + script_name 判断是否已成功，避免重复执行

CREATE TABLE IF NOT EXISTS `saas_schema_migrate_log` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `tenant_code` varchar(64) NOT NULL COMMENT '租户编码',
  `service_code` varchar(64) NOT NULL COMMENT '服务标识（对应脚本目录）',
  `script_name` varchar(255) NOT NULL COMMENT '正向脚本文件名',
  `script_checksum` varchar(64) DEFAULT NULL COMMENT '正向脚本内容校验值',
  `exec_status` tinyint NOT NULL COMMENT '执行状态：1成功 2失败 3已回滚',
  `error_msg` varchar(1000) DEFAULT NULL COMMENT '失败或回滚异常信息',
  `exec_time` datetime DEFAULT NULL COMMENT '正向执行时间',
  `rollback_time` datetime DEFAULT NULL COMMENT '回滚执行时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_service_script` (`tenant_code`, `service_code`, `script_name`),
  KEY `idx_service_status` (`service_code`, `exec_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户schema增量迁移执行日志';
