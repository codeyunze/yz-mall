CREATE TABLE tcc_storage
(
    id           BIGINT   NOT NULL PRIMARY KEY COMMENT '主键id',
    product_id   BIGINT   NOT NULL COMMENT '商品id',
    num          INT      NOT NULL DEFAULT 0 COMMENT '库存数量',
    freeze_count INT      NOT NULL DEFAULT 0 COMMENT '冻结库存数量',
    create_date  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_date  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    invalid      TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除，0：有效数据；1：无效数据'
) ENGINE = INNODB
  CHARSET = utf8 COMMENT = '库存信息';

INSERT INTO tcc_storage (id, product_id, num, freeze_count, create_date, update_date, invalid) VALUES (100001, 2024011600001, 10, 0, '2024-01-16 12:46:23', '2024-01-16 12:46:23', 0);



CREATE TABLE tcc_order
(
    id          BIGINT         NOT NULL PRIMARY KEY COMMENT '主键id',
    account_id  BIGINT         NOT NULL COMMENT '用户ID',
    product_id  BIGINT         NOT NULL COMMENT '商品id',
    num         INT            NOT NULL DEFAULT 0 COMMENT '数量',
    money       DECIMAL(15, 2) NOT NULL DEFAULT '0.00' COMMENT '金额',
    state       TINYINT        NOT NULL DEFAULT 0 COMMENT '订单状态，0：下单未支付；1：下单已支付；-1：下单失败',
    create_date DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_date DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    invalid     TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除，0：有效数据；1：无效数据'
) ENGINE = INNODB
  CHARSET = utf8 COMMENT = '订单信息';

CREATE TABLE tcc_account
(
    id           BIGINT         NOT NULL PRIMARY KEY COMMENT '主键id',
    `name`       VARCHAR(36)    NOT NULL COMMENT '姓名',
    cash_balance DECIMAL(15, 2) NOT NULL DEFAULT '0.00' COMMENT '余额',
    freeze_money DECIMAL(15, 2) NOT NULL DEFAULT '0.00' COMMENT '冻结金额',
    create_date  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_date  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    invalid      TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除，0：有效数据；1：无效数据'
) ENGINE = INNODB
  CHARSET = utf8 COMMENT = '账号信息';

INSERT INTO tcc_account (id, name, cash_balance, freeze_money, create_date, update_date, invalid) VALUES (200001, '尼古拉斯·张三', 1000.00, 0.00, '2024-01-16 12:44:57', '2024-01-16 12:44:57', 0);


-- -------------------------------- The script use tcc fence  --------------------------------
CREATE TABLE IF NOT EXISTS `tcc_fence_log`
(
    `xid`          VARCHAR(128) NOT NULL COMMENT 'global id',
    `branch_id`    BIGINT       NOT NULL COMMENT 'branch id',
    `action_name`  VARCHAR(64)  NOT NULL COMMENT 'action name',
    `status`       TINYINT      NOT NULL COMMENT 'status(tried:1;committed:2;rollbacked:3;suspended:4)',
    `gmt_create`   DATETIME(3)  NOT NULL COMMENT 'create time',
    `gmt_modified` DATETIME(3)  NOT NULL COMMENT 'update time',
    PRIMARY KEY (`xid`, `branch_id`),
    KEY `idx_gmt_modified` (`gmt_modified`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;