
CREATE TABLE `pms_sku`
(
    `id`               bigint                                                        NOT NULL COMMENT '主键标识',
    `product_id`       bigint                                                        NOT NULL COMMENT '商品信息Id',
    `sku_code`         varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT 'SKU编码(商品编码)，唯一',
    `sku_name`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'SKU名称',
    `price_fee`        bigint                                                        NOT NULL COMMENT '售价(单位分)',
    `market_price_fee` bigint                                                        NOT NULL COMMENT '市场价(单位分)',
    `status`           int                                                           NOT NULL DEFAULT '1' COMMENT '状态（1:启用, 0:禁用, -1:删除）',
    `album_pics`       varchar(164) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT NULL COMMENT '商品图片id，限制为5张，以逗号分割',
    `create_time`      datetime                                                               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      datetime                                                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `invalid`          bigint                                                        NOT NULL DEFAULT '0' COMMENT '数据是否有效：0数据有效',
    `create_id`        bigint                                                                 DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `sku_code` (`sku_code`),
    KEY `idx_pms_sku_product_id` (`product_id`),
    KEY `idx_pms_sku_sku_code` (`sku_code`)
) ENGINE = InnoDB COMMENT ='商品SKU表';

CREATE TABLE `pms_stock`
(
    `id`              bigint NOT NULL COMMENT '主键标识',
    `create_id`       bigint   DEFAULT NULL COMMENT '创建人',
    `create_time`     datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_id`       bigint   DEFAULT NULL COMMENT '更新人',
    `update_time`     datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `invalid`         int      DEFAULT '0' COMMENT '数据是否有效：0数据有效',
    `product_id`      bigint   DEFAULT NULL COMMENT '商品Id',
    `quantity`        int      DEFAULT '0' COMMENT '商品库存数量',
    `locked_quantity` int      DEFAULT '0' COMMENT '锁定商品库存数量',
    `sku_id`          bigint   DEFAULT NULL COMMENT '商品实例Id（pms_sku表id）',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_pms_stock_sku_id` (`sku_id`, `quantity`)
) ENGINE = InnoDB
  ROW_FORMAT = DYNAMIC COMMENT ='商品库存表';

CREATE TABLE `test`
(
    `id` int NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;