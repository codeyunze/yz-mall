-- 创建表（去掉MySQL的COMMENT语法）
CREATE TABLE IF NOT EXISTS sys_user (
                                        id          BIGINT PRIMARY KEY NOT NULL,
                                        create_time TIMESTAMP,
                                        update_time TIMESTAMP,
                                        invalid     BIGINT DEFAULT 0,
                                        phone       VARCHAR(11) NOT NULL,
                                        email       VARCHAR(50),
                                        password    VARCHAR(255),
                                        balance     DECIMAL(15, 2) DEFAULT 0.00,
                                        username    VARCHAR(10) NOT NULL,
                                        status      INT DEFAULT 1 NOT NULL,
                                        avatar      VARCHAR(100),
                                        sex         INT DEFAULT 0 NOT NULL
);

-- 添加表注释
COMMENT ON TABLE sys_user IS '系统-用户表';

-- 添加列注释
COMMENT ON COLUMN sys_user.id IS '主键标识';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_time IS '更新时间';
COMMENT ON COLUMN sys_user.invalid IS '数据是否有效：0数据有效';
COMMENT ON COLUMN sys_user.phone IS '手机号';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.password IS '密码';
COMMENT ON COLUMN sys_user.balance IS '账户余额';
COMMENT ON COLUMN sys_user.username IS '昵称';
COMMENT ON COLUMN sys_user.status IS '状态1-启用,0-停用';
COMMENT ON COLUMN sys_user.avatar IS '头像';
COMMENT ON COLUMN sys_user.sex IS '状态1-女,0-男';

-- 创建唯一约束（H2使用CREATE INDEX语法）
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_user_email ON sys_user(email, invalid);
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_user_phone ON sys_user(invalid, phone);



INSERT INTO sys_user (id, create_time, update_time, invalid, phone, email, password, balance, username, status, avatar,
                      sex)
VALUES (1867495856688271360, '2024-12-13 17:04:43', '2025-04-26 22:31:19', 0, '15300000017', null, 'ABCdef123', 676.90,
        '小亮', 1, null, 0);
