CREATE TABLE IF NOT EXISTS `demo` (
    `id`   int         NOT NULL COMMENT '主键',
    `name` varchar(36)          DEFAULT NULL COMMENT '名称',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT ='迁移测试演示表';
