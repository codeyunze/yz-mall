-- =============================================================================
-- P0/P1：商品 / SKU / 购物车 / 库存 / 订单表结构改造
-- 依据：docs/商品SKU订单购物车表结构设计建议.md
-- 说明：请在业务低峰执行；执行前备份；按章节顺序执行
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. 订单行：补 SKU 快照，唯一键改为 order_id + sku_id
-- -----------------------------------------------------------------------------
ALTER TABLE `oms_order_relation_product`
  ADD COLUMN `sku_id` bigint NULL COMMENT '下单SKU Id（快照关联）' AFTER `product_id`,
  ADD COLUMN `sku_code` varchar(64) NULL COMMENT 'SKU编码快照' AFTER `sku_id`,
  ADD COLUMN `sku_name` varchar(255) NULL COMMENT 'SKU名称快照' AFTER `sku_code`,
  ADD COLUMN `refund_quantity` int NOT NULL DEFAULT 0 COMMENT '已退款数量' AFTER `product_quantity`;

-- 历史回填：优先取该商品下唯一有效 SKU；多规格取 id 最小的一条（仅兜底，需运营核对）
UPDATE `oms_order_relation_product` o
INNER JOIN (
  SELECT `product_id`, MIN(`id`) AS `sku_id`
  FROM `pms_sku`
  WHERE `invalid` = 0
  GROUP BY `product_id`
) s ON o.`product_id` = s.`product_id`
LEFT JOIN `pms_sku` sku ON sku.`id` = s.`sku_id`
SET o.`sku_id` = s.`sku_id`,
    o.`sku_code` = sku.`sku_code`,
    o.`sku_name` = sku.`sku_name`,
    o.`product_attributes` = IFNULL(NULLIF(o.`product_attributes`, ''), sku.`sku_name`),
    o.`album_pics` = IF(o.`album_pics` IS NULL OR o.`album_pics` = '', sku.`album_pics`, o.`album_pics`)
WHERE o.`sku_id` IS NULL;

-- 仍无法回填的脏数据（商品无 SKU）禁止继续：人工处理后再执行后续 NOT NULL
-- SELECT * FROM oms_order_relation_product WHERE sku_id IS NULL;

ALTER TABLE `oms_order_relation_product`
  MODIFY COLUMN `sku_id` bigint NOT NULL COMMENT '下单SKU Id（快照关联）';

ALTER TABLE `oms_order_relation_product`
  DROP INDEX `uk_oms_order_product`,
  ADD UNIQUE KEY `uk_oms_order_sku` (`order_id`, `sku_id`, `invalid`),
  ADD KEY `idx_oms_order_item_sku` (`sku_id`);

-- -----------------------------------------------------------------------------
-- 2. 订单头：运费、售后状态
-- -----------------------------------------------------------------------------
ALTER TABLE `oms_order`
  ADD COLUMN `freight_amount` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '运费金额' AFTER `discount_amount`,
  ADD COLUMN `refund_status` int NOT NULL DEFAULT 0 COMMENT '售后状态：0无售后；1售后中；2部分退款；3全额退款' AFTER `order_status`;

-- -----------------------------------------------------------------------------
-- 3. 购物车：sku 必填、勾选态、唯一键改为用户+SKU（保留 invalid 以兼容逻辑删除）
-- -----------------------------------------------------------------------------
-- 清理无规格脏数据
DELETE FROM `pms_shop_cart` WHERE `sku_id` IS NULL;

-- 合并同用户同 SKU 多行（保留 id 最小，数量累加）
UPDATE `pms_shop_cart` c
INNER JOIN (
  SELECT MIN(`id`) AS keep_id, `user_id`, `sku_id`, SUM(`quantity`) AS total_qty
  FROM `pms_shop_cart`
  WHERE `invalid` = 0
  GROUP BY `user_id`, `sku_id`
  HAVING COUNT(*) > 1
) t ON c.`user_id` = t.`user_id` AND c.`sku_id` = t.`sku_id` AND c.`invalid` = 0
SET c.`quantity` = t.`total_qty`
WHERE c.`id` = t.`keep_id`;

DELETE c FROM `pms_shop_cart` c
INNER JOIN (
  SELECT MIN(`id`) AS keep_id, `user_id`, `sku_id`
  FROM `pms_shop_cart`
  WHERE `invalid` = 0
  GROUP BY `user_id`, `sku_id`
  HAVING COUNT(*) > 1
) t ON c.`user_id` = t.`user_id` AND c.`sku_id` = t.`sku_id` AND c.`invalid` = 0
WHERE c.`id` <> t.`keep_id`;

ALTER TABLE `pms_shop_cart`
  MODIFY COLUMN `sku_id` bigint NOT NULL COMMENT '商品SKU id',
  ADD COLUMN `checked` tinyint NOT NULL DEFAULT 1 COMMENT '是否勾选结算：0否；1是' AFTER `quantity`;

ALTER TABLE `pms_shop_cart`
  DROP INDEX `uk_oms_cart_user_sku`,
  ADD UNIQUE KEY `uk_cart_user_sku` (`user_id`, `sku_id`, `invalid`);

-- -----------------------------------------------------------------------------
-- 4. 库存：清理重复后加 sku 唯一；预留默认仓
-- -----------------------------------------------------------------------------
-- 合并重复库存行（同 sku 数量相加到最小 id）
UPDATE `pms_stock` s
INNER JOIN (
  SELECT MIN(`id`) AS keep_id, `sku_id`,
         SUM(IFNULL(`quantity`, 0)) AS total_qty,
         SUM(IFNULL(`locked_quantity`, 0)) AS total_locked
  FROM `pms_stock`
  WHERE `sku_id` IS NOT NULL AND (`invalid` = 0 OR `invalid` IS NULL)
  GROUP BY `sku_id`
  HAVING COUNT(*) > 1
) t ON s.`id` = t.`keep_id`
SET s.`quantity` = t.`total_qty`,
    s.`locked_quantity` = t.`total_locked`;

DELETE s FROM `pms_stock` s
INNER JOIN (
  SELECT MIN(`id`) AS keep_id, `sku_id`
  FROM `pms_stock`
  WHERE `sku_id` IS NOT NULL AND (`invalid` = 0 OR `invalid` IS NULL)
  GROUP BY `sku_id`
  HAVING COUNT(*) > 1
) t ON s.`sku_id` = t.`sku_id` AND (s.`invalid` = 0 OR s.`invalid` IS NULL)
WHERE s.`id` <> t.`keep_id`;

ALTER TABLE `pms_stock`
  ADD COLUMN `warehouse_id` bigint NOT NULL DEFAULT 0 COMMENT '仓库Id，0表示默认仓' AFTER `sku_id`;

-- 回填 product_id（以 SKU 为准）
UPDATE `pms_stock` st
INNER JOIN `pms_sku` sku ON sku.`id` = st.`sku_id`
SET st.`product_id` = sku.`product_id`
WHERE st.`sku_id` IS NOT NULL;

ALTER TABLE `pms_stock`
  MODIFY COLUMN `sku_id` bigint NOT NULL COMMENT '商品实例Id（pms_sku表id）',
  ADD UNIQUE KEY `uk_stock_sku_wh` (`sku_id`, `warehouse_id`);

-- -----------------------------------------------------------------------------
-- 5. SKU：销售属性快照字段
-- -----------------------------------------------------------------------------
ALTER TABLE `pms_sku`
  ADD COLUMN `attrs_json` varchar(1024) NULL COMMENT '销售属性JSON，如[{"name":"颜色","value":"红"}]' AFTER `album_pics`,
  ADD COLUMN `attrs_key` varchar(512) NULL COMMENT '属性组合键，如颜色:红;尺码:XL' AFTER `attrs_json`;

-- 有数据后再加唯一（先允许空）：有效 SKU 同商品属性组合唯一
-- ALTER TABLE `pms_sku` ADD UNIQUE KEY `uk_sku_product_attrs` (`product_id`, `attrs_key`, `invalid`);

-- -----------------------------------------------------------------------------
-- 6. 商品图片表 + 从 album_pics 迁移
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms_product_image` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `biz_type` tinyint NOT NULL COMMENT '业务类型：0=SPU；1=SKU',
  `biz_id` bigint NOT NULL COMMENT '业务Id（product_id 或 sku_id）',
  `file_id` bigint NOT NULL COMMENT '文件服务文件Id',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序，数值越小越靠前',
  `is_main` tinyint NOT NULL DEFAULT 0 COMMENT '是否主图：0否；1是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  KEY `idx_product_image_biz` (`biz_type`, `biz_id`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品/SKU图片表';

-- 迁移说明：album_pics 逗号分隔，需应用侧或后续脚本拆分写入；此处不强制清空旧字段（双读过渡）

-- -----------------------------------------------------------------------------
-- 7. 库存流水表
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms_stock_log` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `sku_id` bigint NOT NULL COMMENT 'SKU Id',
  `product_id` bigint DEFAULT NULL COMMENT '商品Id（冗余）',
  `warehouse_id` bigint NOT NULL DEFAULT 0 COMMENT '仓库Id，0默认仓',
  `change_type` int NOT NULL COMMENT '变更类型：1入库；2出库扣减；3锁库；4解锁；5回补',
  `change_qty` int NOT NULL COMMENT '变更数量（正数）',
  `before_qty` int NOT NULL DEFAULT 0 COMMENT '变更前可售数量',
  `after_qty` int NOT NULL DEFAULT 0 COMMENT '变更后可售数量',
  `biz_no` varchar(64) DEFAULT NULL COMMENT '业务单号（订单号/入库单号等）',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单Id',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  KEY `idx_stock_log_sku_time` (`sku_id`, `create_time`),
  KEY `idx_stock_log_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品库存变更流水';

-- -----------------------------------------------------------------------------
-- 8. 退款明细表（按订单行/SKU 退，头表仍用 oms_order_refund）
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `oms_order_refund_item` (
  `id` bigint NOT NULL COMMENT '主键标识',
  `refund_id` bigint NOT NULL COMMENT '退款单Id',
  `order_id` bigint NOT NULL COMMENT '订单Id',
  `order_item_id` bigint NOT NULL COMMENT '订单行Id（oms_order_relation_product.id）',
  `product_id` bigint NOT NULL COMMENT '商品Id',
  `sku_id` bigint NOT NULL COMMENT 'SKU Id',
  `quantity` int NOT NULL DEFAULT 0 COMMENT '退款数量',
  `refund_amount` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '本行退款金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `invalid` bigint NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
  PRIMARY KEY (`id`),
  KEY `idx_refund_item_refund` (`refund_id`),
  KEY `idx_refund_item_order` (`order_id`),
  KEY `idx_refund_item_sku` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单退款明细表';
