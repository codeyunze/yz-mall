-- =============================================================================
-- 金额全链路统一为「分」（bigint）
-- 前置：建议已执行 p0_product_sku_order_cart_refactor.sql（含 freight_amount）
-- 规则：原 decimal 元 ×100 后改 bigint；注释标明单位分
-- =============================================================================

-- ---------- 商品 ----------
UPDATE `pms_product`
SET `product_price` = ROUND(IFNULL(`product_price`, 0) * 100)
WHERE `product_price` IS NOT NULL;

ALTER TABLE `pms_product`
  MODIFY COLUMN `product_price` bigint DEFAULT 0 COMMENT '商品展示价/原价（分，列表可用 SKU 最低价覆盖）';

-- ---------- 订单头 ----------
UPDATE `oms_order`
SET `total_amount` = ROUND(IFNULL(`total_amount`, 0) * 100),
    `discount_amount` = ROUND(IFNULL(`discount_amount`, 0) * 100),
    `pay_amount` = ROUND(IFNULL(`pay_amount`, 0) * 100),
    `freight_amount` = ROUND(IFNULL(`freight_amount`, 0) * 100);

ALTER TABLE `oms_order`
  MODIFY COLUMN `total_amount` bigint NOT NULL DEFAULT 0 COMMENT '订单总金额（分）',
  MODIFY COLUMN `discount_amount` bigint NOT NULL DEFAULT 0 COMMENT '优惠金额（分）',
  MODIFY COLUMN `freight_amount` bigint NOT NULL DEFAULT 0 COMMENT '运费金额（分）',
  MODIFY COLUMN `pay_amount` bigint NOT NULL DEFAULT 0 COMMENT '订单实际应付金额（分）';

-- ---------- 订单行 ----------
UPDATE `oms_order_relation_product`
SET `discount_amount` = ROUND(IFNULL(`discount_amount`, 0) * 100),
    `real_amount` = ROUND(IFNULL(`real_amount`, 0) * 100),
    `product_price` = ROUND(IFNULL(`product_price`, 0) * 100);

ALTER TABLE `oms_order_relation_product`
  MODIFY COLUMN `discount_amount` bigint NOT NULL DEFAULT 0 COMMENT '商品优惠金额（分）',
  MODIFY COLUMN `real_amount` bigint NOT NULL DEFAULT 0 COMMENT '商品优惠后实际单价（分）',
  MODIFY COLUMN `product_price` bigint NOT NULL DEFAULT 0 COMMENT '下单时单价（分）';

-- ---------- 退款 ----------
UPDATE `oms_order_refund`
SET `refund_amount` = ROUND(IFNULL(`refund_amount`, 0) * 100);

ALTER TABLE `oms_order_refund`
  MODIFY COLUMN `refund_amount` bigint NOT NULL DEFAULT 0 COMMENT '退款金额（分）';

-- 退款明细表（若已建）
UPDATE `oms_order_refund_item`
SET `refund_amount` = ROUND(IFNULL(`refund_amount`, 0) * 100);

ALTER TABLE `oms_order_refund_item`
  MODIFY COLUMN `refund_amount` bigint NOT NULL DEFAULT 0 COMMENT '本行退款金额（分）';

-- ---------- 用户余额 ----------
UPDATE `sys_user`
SET `balance` = ROUND(IFNULL(`balance`, 0) * 100);

ALTER TABLE `sys_user`
  MODIFY COLUMN `balance` bigint NOT NULL DEFAULT 0 COMMENT '账户余额（分）';
