
# master 主节点相关命令
# 查看主节点信息
show master status ;

# 创建指定用户
create user 'replication_user'@'%' identified by '123456';
# 授予指定用户复制所有数据库和表的相关操作（一般用于主从复制）
grant replication slave on *.* to 'replication_user'@'%';
# 刷新权限以确保立即生效
flush privileges ;


# slave 从节点相关命令
# 设置同步主节点
change master to
    master_host = '127.0.0.1',
    master_port = 3306,
    master_user = 'replication_user',
    master_password = '123456',
    master_log_file = 'master-bin.000002',
    master_log_pos = 6580,
    get_master_public_key = 1;
# 开启slave从节点
start slave ;
# 关闭从节点
stop slave ;
# 查看从节点的主从同步状态
show slave status ;

