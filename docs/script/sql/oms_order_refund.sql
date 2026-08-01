-- 申请退款 V1：退款申请表 + 订单状态 COMMENT 扩展（7退款中 / 8已退款）
-- 执行前请确认目标库；本仓库 MCP 只读账号无法代执行 DDL。

-- 订单退款申请表
CREATE TABLE `oms_order_refund` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `refund_no` varchar(32) NOT NULL COMMENT '退款单号',
  `order_id` bigint NOT NULL COMMENT '订单id',
  `order_code` varchar(64) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '申请人用户id',
  `business_org_id` bigint DEFAULT NULL COMMENT '商家组织id',
  `refund_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额（等于订单实付金额）',
  `reason_type` int DEFAULT NULL COMMENT '原因类型（字典，可选）',
  `reason` varchar(500) NOT NULL COMMENT '退款说明',
  `refund_status` int NOT NULL DEFAULT '0' COMMENT '退款状态：0待审核；1已通过；2已拒绝；3已取消',
  `audit_user_id` bigint DEFAULT NULL COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_remark` varchar(500) DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT '0' COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status_time` (`refund_status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单退款申请表';

ALTER TABLE `oms_order`
  MODIFY COLUMN `order_status` int NOT NULL DEFAULT '0'
  COMMENT '订单状态：0待付款；1待发货；2已发货；3待收货；4已完成；5已关闭/已取消；6无效订单；7退款中；8已退款';

-- 可选：后台菜单「退款审核」（父级与「我的订单/订单管理」相同：1882262180350058496）
-- 执行后请给对应角色授权该菜单
INSERT INTO `sys_menu` (
  `id`, `parent_id`, `menu_type`, `title`, `name`, `path`, `component`, `sort`,
  `redirect`, `icon`, `extra_icon`, `enter_transition`, `leave_transition`, `active_path`, `auths`,
  `frame_src`, `frame_loading`, `keep_alive`, `hidden_tag`, `fixed_tag`, `show_link`, `show_parent`, `invalid`
) VALUES (
  1945000000000000001, 1882262180350058496, 0, '退款审核', 'OmsOrderRefundPage',
  '/mall/order/refund', 'views/mall/order/refund/index.vue', 22,
  '', 'fa-solid:clipboard-list', '', '', '', '', '',
  '', 1, 0, 0, 0, 1, 0, 0
);
