#!/bin/bash

# 配置项
BACKUP_DIR="/data/backup/mysql"             # 备份文件存储目录
MYSQL_USER="root"                           # MySQL 用户名
MYSQL_PASS="12345678"                       # MySQL 密码
DATABASE_NAME="mydb"                        # 要备份的数据库名，可改为 all_databases 全库备份
DATE=$(date +"%Y%m%d_%H%M%S")               # 时间戳
BACKUP_FILE="$BACKUP_DIR/${DATABASE_NAME}_backup_$DATE.sql.gz"

# 日志输出
LOG_FILE="$BACKUP_DIR/backup.log"

# 开始备份
echo "[$(date)] 开始备份数据库: $DATABASE_NAME" >> $LOG_FILE

# 使用 mysqldump 备份并用 gzip 压缩
mysqldump -u"$MYSQL_USER" -p"$MYSQL_PASS" \
          --single-transaction \
          --routines \
          --triggers \
          --set-gtid-purged=OFF \
          "$DATABASE_NAME" | gzip > "$BACKUP_FILE"

# 检查是否备份成功
if [ $? -eq 0 ]; then
    echo "[$(date)] 备份成功: $BACKUP_FILE" >> $LOG_FILE
else
    echo "[$(date)] 备份失败!" >> $LOG_FILE
fi

# 删除7天前的旧备份
find "$BACKUP_DIR" -name "*.sql.gz" -mtime +7 -delete
echo "[$(date)] 已清理7天前的旧备份" >> $LOG_FILE