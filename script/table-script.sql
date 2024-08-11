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

alter table mall_stock modify `created_time` DATETIME default current_timestamp COMMENT '创建时间';
alter table mall_stock modify `updated_time` DATETIME on update current_timestamp COMMENT '更新时间';


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

alter table mall_product modify `created_time` DATETIME default current_timestamp COMMENT '创建时间';
alter table mall_product modify `updated_time` DATETIME on update current_timestamp COMMENT '更新时间';


alter table base_user modify `id` VARCHAR(32) NOT NULL COMMENT '主键标识';

alter table mall.base_user add balance decimal(15, 2) default 0 comment '账户余额';

alter table mall.sys_unqid
    modify updated_time datetime null on update current_timestamp comment '更新时间';



create table test_serial_number
(
    id           int auto_increment primary key,
    created_time datetime default current_timestamp comment '创建时间',
    code         varchar(32) not null comment '序列号'
) comment = '测试表-序列号生成';

insert into test_serial_number (code) values ('1000001');

select * from test_serial_number;


CREATE TABLE sys_application
(
    `id`            VARCHAR(32) NOT NULL COMMENT '主键标识',
    `created_id`    VARCHAR(32) COMMENT '创建人',
    `created_time`  DATETIME DEFAULT current_timestamp COMMENT '创建时间',
    `updated_id`    VARCHAR(32) COMMENT '更新人',
    `updated_time`  DATETIME COMMENT '更新时间',
    `invalid`       INT      DEFAULT 0 COMMENT '数据是否有效：0数据有效',
    `client_id`     VARCHAR(36) NOT NULL COMMENT '应用id',
    `client_secret` VARCHAR(36) NOT NULL COMMENT '应用密钥',
    `client_name`   VARCHAR(36) NOT NULL COMMENT '应用名称',
    `remark`        VARCHAR(255) COMMENT '备注说明信息',
    PRIMARY KEY (id)
) COMMENT = '应用配置';


CREATE UNIQUE INDEX idx_sys_app_clientId ON sys_application(client_id);

INSERT INTO sys_application (id, created_id, created_time, updated_id, updated_time, invalid, client_id, client_secret, client_name, remark) VALUES ('1', 'admin', '2024-08-11 20:19:32', 'admin', null, 0, '1001', 'aaaa-bbbb-cccc-dddd-eeee', '默认应用', '系统自带应用客户端');
