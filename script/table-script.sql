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










-- 系统表====================================================================================================================

CREATE TABLE sys_org
(
    `id`          BIGINT       NOT NULL COMMENT '主键标识',
    `create_time` DATETIME              DEFAULT current_timestamp COMMENT '创建时间',
    `update_time` DATETIME              DEFAULT current_timestamp on update current_timestamp COMMENT '更新时间',
    `invalid`     INT          NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
    `org_name`    VARCHAR(255) NOT NULL COMMENT '组织名称',
    `user_id`     BIGINT       NOT NULL COMMENT '组织所属用户',
    `parent_id`   BIGINT       NOT NULL DEFAULT -1 COMMENT '上级组织',
    `status`      INT          NOT NULL DEFAULT 1 COMMENT '状态1-启用,0-停用',
    `remark`      VARCHAR(100) COMMENT '备注说明',
    `sort`        INT          NOT NULL DEFAULT 99 COMMENT '排序',
    `org_path_id` VARCHAR(100) NOT NULL COMMENT '组织路径',
    PRIMARY KEY (id)
) COMMENT = '系统-组织表';

CREATE INDEX ix_sys_org ON sys_org (user_id, org_name);
CREATE INDEX uk_org_path ON sys_org (org_path_id);

CREATE TABLE sys_role
(
    `id`          bigint      NOT NULL COMMENT '主键标识',
    `create_time` DATETIME             DEFAULT current_timestamp COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT current_timestamp ON UPDATE current_timestamp COMMENT '更新时间',
    `invalid`     INT         NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
    `role_code`   VARCHAR(36) NOT NULL COMMENT '角色编码',
    `role_name`   VARCHAR(36) NOT NULL COMMENT '角色名称',
    `org_id`      BIGINT      NOT NULL DEFAULT -1 COMMENT '所属组织',
    PRIMARY KEY (id)
) COMMENT = '系统-角色数据表';

alter table sys_role add column `status` INT  DEFAULT 1 COMMENT '状态1-启用,0-停用';

CREATE UNIQUE INDEX uk_sys_role ON sys_role (org_id, role_code, invalid);


CREATE TABLE sys_permission
(
    `id`              BIGINT      NOT NULL COMMENT '主键标识',
    `create_time`     DATETIME    NOT NULL DEFAULT current_timestamp COMMENT '创建时间',
    `update_time`     DATETIME             DEFAULT current_timestamp ON UPDATE current_timestamp COMMENT '更新时间',
    `invalid`         BIGINT      NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
    `permission_code` VARCHAR(36) NOT NULL COMMENT '权限编码',
    `permission_name` VARCHAR(36) COMMENT '权限名称'
    PRIMARY KEY (id)
) COMMENT = '系统-权限数据表';

CREATE UNIQUE INDEX uk_sys_permission_role ON sys_permission (role_id, permission_code, invalid);


CREATE TABLE sys_user_relation_role
(
    `id`          BIGINT NOT NULL COMMENT '主键标识',
    `create_time` DATETIME        DEFAULT current_timestamp COMMENT '创建时间',
    `update_time` DATETIME        DEFAULT current_timestamp ON UPDATE current_timestamp COMMENT '更新时间',
    `invalid`     INT    NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
    `role_id`     BIGINT NOT NULL COMMENT '关联角色Id',
    `relation_id` BIGINT NOT NULL COMMENT '关联用户Id/组织Id',
    PRIMARY KEY (id)
) COMMENT = '系统-用户与组织关联角色表';

CREATE UNIQUE INDEX uk_sys_role_relation_by_user ON sys_user_relation_role (relation_id, invalid, role_id);
CREATE INDEX ix_sys_role_relation_by_role ON sys_user_relation_role (role_id, invalid, relation_id);



CREATE TABLE sys_user_relation_org
(
    `id`          BIGINT NOT NULL COMMENT '主键标识',
    `create_time` DATETIME        DEFAULT current_timestamp COMMENT '创建时间',
    `update_time` DATETIME        DEFAULT current_timestamp ON UPDATE current_timestamp COMMENT '更新时间',
    `invalid`     INT    NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
    `user_id`     BIGINT NOT NULL COMMENT '用户Id',
    `org_id`      BIGINT NOT NULL COMMENT '组织Id',
    PRIMARY KEY (id)
) COMMENT = '系统-用户关联组织数据表';

CREATE UNIQUE INDEX uk_sys_user_relation_org ON sys_user_relation_org (user_id, org_id, invalid);
CREATE INDEX ix_sys_org_have_user ON sys_user_relation_org (org_id, user_id);


CREATE TABLE sys_user
(
    `id`          BIGINT                      NOT NULL COMMENT '主键标识',
    `create_time` DATETIME       DEFAULT current_timestamp COMMENT '创建时间',
    `update_time` DATETIME       DEFAULT current_timestamp ON UPDATE current_timestamp COMMENT '更新时间',
    `invalid`     INT            DEFAULT 0 COMMENT '数据是否有效：0数据有效',
    `phone`       VARCHAR(11)                 NOT NULL COMMENT '手机号',
    `email`       VARCHAR(50) COMMENT '邮箱',
    `password`    VARCHAR(255) COMMENT '密码',
    `balance`     decimal(15, 2) default 0.00 null comment '账户余额',
    PRIMARY KEY (id)
) COMMENT = '系统-用户表';

alter table sys_user add column name varchar(32) not null comment '昵称';

CREATE UNIQUE INDEX uk_sys_user_phone ON sys_user (invalid, phone);
CREATE UNIQUE INDEX uk_sys_user_email ON sys_user (email, invalid);


CREATE TABLE sys_menu
(
    `id`               INT      NOT NULL COMMENT '主键标识',
    `create_time`      DATETIME NOT NULL DEFAULT current_timestamp COMMENT '创建时间',
    `update_time`      DATETIME          DEFAULT current_timestamp COMMENT '更新时间',
    `invalid`          INT      NOT NULL DEFAULT 0 COMMENT '数据是否有效：0数据有效',
    `parent_id`        VARCHAR(255) COMMENT '上级菜单',
    `menu_type`        INT COMMENT '菜单类型0-菜单,1-iframe,2-外链接,3-按钮,4-接口',
    `title`            VARCHAR(50) COMMENT '菜单名称',
    `name`             VARCHAR(36) COMMENT '路由名称',
    `path`             VARCHAR(50) COMMENT '路由路径',
    `component`        VARCHAR(50) COMMENT '组件路径',
    `sort`             INT COMMENT '排序',
    `redirect`         VARCHAR(50) COMMENT '路由重定向',
    `icon`             VARCHAR(100) COMMENT '菜单图标',
    `extra_icon`       VARCHAR(100) COMMENT '菜单右侧额外图标',
    `enter_transition` VARCHAR(10) COMMENT '进场动画',
    `leave_transition` VARCHAR(10) COMMENT '离场动画',
    `active_path`      VARCHAR(100) COMMENT '菜单激活',
    `auths`            VARCHAR(50) COMMENT '按钮权限标识',
    `frame_src`        VARCHAR(100) COMMENT '链接地址(嵌入iframe链接地址)',
    `frame_loading`    INT               DEFAULT 1 COMMENT '加载动画(内嵌的iframe页面是否开启首次加载动画)',
    `keep_alive`       INT               DEFAULT 0 COMMENT '缓存页面(是否缓存该路由页面)',
    `hidden_tag`       INT               DEFAULT 1 COMMENT '标签页(当前菜单名称或自定义信息禁止添加到标签页)',
    `fixed_tag`        INT               DEFAULT 0 COMMENT '固定标签页(当前菜单名称是否固定显示在标签页且不可关闭)',
    `show_link`        INT               DEFAULT 1 COMMENT '是否显示该菜单',
    `show_parent`      INT               DEFAULT 0 COMMENT '是否显示父级菜单',
    PRIMARY KEY (id)
) COMMENT = '系统-菜单资源表';


CREATE TABLE sys_role_relation_menu
(
    `id`          BIGINT NOT NULL COMMENT '主键标识',
    `create_time` DATETIME DEFAULT current_timestamp COMMENT '创建时间',
    `update_time` DATETIME DEFAULT current_timestamp COMMENT '更新时间',
    `invalid`     INT      DEFAULT 0 COMMENT '数据是否有效：0数据有效',
    `role_id`     BIGINT NOT NULL COMMENT '角色Id',
    `menu_id`     BIGINT NOT NULL COMMENT '菜单Id',
    PRIMARY KEY (id)
) COMMENT = '系统-角色关联菜单表';
CREATE UNIQUE INDEX uk_sys_role_menu ON sys_role_relation_menu (role_id, menu_id, invalid);

