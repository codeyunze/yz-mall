DROP TABLE IF EXISTS mall_stock;
CREATE TABLE mall_stock
(
    `id`           VARCHAR(32) NOT NULL COMMENT '主键标识',
    `created_id`   VARCHAR(32) COMMENT '创建人',
    `created_time` DATETIME COMMENT '创建时间',
    `updated_id`   VARCHAR(32) COMMENT '更新人',
    `updated_time` DATETIME COMMENT '更新时间',
    `invalid`      INT DEFAULT 0 COMMENT '数据是否有效：0数据有效',
    `product_id`   VARCHAR(32) COMMENT '商品信息Id',
    `quantity`     INT DEFAULT 0 COMMENT '商品库存数量',
    PRIMARY KEY (id)
) COMMENT = '商品库存表';


DROP TABLE IF EXISTS mall_product;
CREATE TABLE mall_product
(
    `id`             VARCHAR(32) NOT NULL COMMENT '主键标识',
    `created_id`     VARCHAR(32) COMMENT '创建人',
    `created_time`   DATETIME COMMENT '创建时间',
    `updated_id`     VARCHAR(32) COMMENT '更新人',
    `updated_time`   DATETIME COMMENT '更新时间',
    `invalid`        INT DEFAULT 0 COMMENT '数据是否有效：0数据有效',
    `name`           VARCHAR(50) COMMENT '商品名称',
    `price`          DECIMAL(15, 2) COMMENT '商品价格',
    `title`          VARCHAR(50) COMMENT '商品标签',
    `remark`         VARCHAR(255) COMMENT '商品备注信息',
    `publish_status` INT COMMENT '商品上架状态',
    `verify_status`  INT COMMENT '商品审核状态',
    `album_pics`     VARCHAR(164) COMMENT '商品图片id，限制为5张，以逗号分割',
    PRIMARY KEY (id)
) COMMENT = '商品表';

alter table mall_product modify `publish_status` INT default 0 COMMENT '商品上架状态：0：下架，1：上架';
alter table mall_product modify `verify_status` INT default 0 COMMENT '商品审核状态：0：未审核，1：审核通过';
