-- 收货地址增加默认标记：同一用户同时仅一条默认地址（业务层保证）
ALTER TABLE `sys_receipt_info`
  ADD COLUMN `is_default` int NOT NULL DEFAULT '0' COMMENT '是否默认地址：0否；1是' AFTER `receiver_email`;

ALTER TABLE `sys_receipt_info`
  ADD KEY `idx_receipt_user_default` (`create_id`, `is_default`);
