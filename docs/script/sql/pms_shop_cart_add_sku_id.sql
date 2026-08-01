-- 购物车关联 SKU：新增 sku_id，唯一键改为用户+商品+SKU
-- 执行前请确认目标库；只读账号无法代执行。

ALTER TABLE `pms_shop_cart`
  ADD COLUMN `sku_id` bigint DEFAULT NULL COMMENT '商品SKU id' AFTER `product_id`;

ALTER TABLE `pms_shop_cart`
  DROP INDEX `uk_oms_cart_user`,
  ADD UNIQUE KEY `uk_oms_cart_user_sku` (`user_id`, `product_id`, `sku_id`, `invalid`);
