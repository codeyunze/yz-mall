#!/bin/bash

# =============== 配置区（默认值，可通过命令行参数覆盖） ===============
DB_HOST="localhost"
DB_PORT="3306"
DB_USER="root"      # 替换为你的数据库用户名
DB_PASS="123456"    # 替换为你的数据库密码
DB_NAME="db_name"   # 替换为你要备份的数据库名
BACKUP_ROOT="/data/backup/mysql"   # 备份根目录（不含时间戳）

# 大表拆分配置：当表行数超过以下阈值时，按多个 sql 文件备份
ROW_THRESHOLD=100000   # 超过该行数即拆分为多文件（可根据磁盘/内存调整）
CHUNK_SIZE=50000       # 每个数据文件最多包含的行数

# 日志：留空则使用本次备份目录下的 backup.log
LOG_FILE=""
# 日志超过该大小(MB)时自动备份为 .YYYYMMDD_HHMMSS.bak，然后重新记录
LOG_SIZE_LIMIT_MB=10

# 若当前日志文件超过限制则重命名为带时间戳的 .bak
rotate_log_if_needed() {
    [ -n "${1:-}" ] || return 0
    local f="$1" limit=$((LOG_SIZE_LIMIT_MB * 1024 * 1024)) size
    [ -f "$f" ] || return 0
    size=$(wc -c < "$f" 2>/dev/null) || size=0
    [ "${size:-0}" -ge "$limit" ] || return 0
    mv "$f" "${f}.$(date +%Y%m%d_%H%M%S).bak"
}

# =============== 解析命令行参数（未传则使用上方配置区默认值） ===============
show_usage() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项（未指定时使用脚本内配置区默认值）:"
    echo "  -H, --host        数据库主机，如: -H 127.0.0.1"
    echo "  -P, --port        数据库端口，如: -P 3306"
    echo "  -u, --user        数据库用户，如: -u root"
    echo "  -p, --password    数据库密码，如: -p yourpass"
    echo "  -d, --database    数据库名，如: -d mall"
    echo "  -b, --backup-dir  备份根目录，如: -b /data/backup/mysql"
    echo "  -t, --tables      仅备份指定表，多个表用逗号分隔，如: -t user,order,product"
    echo "  -i, --ignore      不备份的表，多个表用逗号分隔；优先级高于 -t"
    echo "  -c, --clean       备份完成后清理 N 天前的旧备份，如: -c 10（不传则不清理）"
    echo "  -h, --help        显示此帮助"
    echo ""
    echo "示例:"
    echo "  $0                                      # 使用配置区默认值，备份库中所有表"
    echo "  $0 -d mall2 -b /backup                  # 备份 mall2 到 /backup"
    echo "  $0 -t user,order -i order_log           # 仅备份 user、order，但排除 order_log"
    echo "  $0 -d mall -c 10                        # 备份 mall 并清理 10 天前备份"
}

CLEAN_DAYS=""
TABLES_INCLUDE=""
TABLES_EXCLUDE=""
while [ $# -gt 0 ]; do
    case "${1}" in
        -h|--help)
            show_usage
            exit 0
            ;;
        -H|--host)
            [ -n "${2:-}" ] || { echo "错误: -H/--host 需要指定主机"; exit 1; }
            DB_HOST="${2}"
            shift 2
            ;;
        -P|--port)
            [ -n "${2:-}" ] || { echo "错误: -P/--port 需要指定端口"; exit 1; }
            DB_PORT="${2}"
            shift 2
            ;;
        -u|--user)
            [ -n "${2:-}" ] || { echo "错误: -u/--user 需要指定用户"; exit 1; }
            DB_USER="${2}"
            shift 2
            ;;
        -p|--password)
            [ -n "${2:-}" ] || { echo "错误: -p/--password 需要指定密码"; exit 1; }
            DB_PASS="${2}"
            shift 2
            ;;
        -d|--database)
            [ -n "${2:-}" ] || { echo "错误: -d/--database 需要指定数据库名"; exit 1; }
            DB_NAME="${2}"
            shift 2
            ;;
        -b|--backup-dir)
            [ -n "${2:-}" ] || { echo "错误: -b/--backup-dir 需要指定备份根目录"; exit 1; }
            BACKUP_ROOT="${2}"
            shift 2
            ;;
        -t|--tables)
            [ -n "${2:-}" ] || { echo "错误: -t/--tables 需要指定表名列表"; exit 1; }
            TABLES_INCLUDE="${2}"
            shift 2
            ;;
        -i|--ignore)
            [ -n "${2:-}" ] || { echo "错误: -i/--ignore 需要指定表名列表"; exit 1; }
            TABLES_EXCLUDE="${2}"
            shift 2
            ;;
        -c|--clean)
            [ -n "${2:-}" ] || { echo "错误: -c/--clean 需要指定天数"; exit 1; }
            CLEAN_DAYS="${2}"
            shift 2
            ;;
        *)
            echo "错误: 未知选项 ${1}"
            echo "使用 $0 -h 查看帮助"
            exit 1
            ;;
    esac
done

# 处理表名列表：逗号分隔转为空格分隔
[ -n "${TABLES_INCLUDE}" ] && TABLES_INCLUDE=$(echo "${TABLES_INCLUDE}" | tr ',' ' ')
[ -n "${TABLES_EXCLUDE}" ] && TABLES_EXCLUDE=$(echo "${TABLES_EXCLUDE}" | tr ',' ' ')

# 实际备份目录（在参数解析后设置）
BACKUP_DIR="${BACKUP_ROOT}/${DB_NAME}_$(date +%Y%m%d_%H%M%S)"

# =============== 脚本主体 ===============
set -e

MYSQL_CMD="mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASS} -N"
DUMP_CMD="mysqldump -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASS} \
          --skip-comments --skip-add-drop-table --skip-triggers --single-transaction --quick"

# 检查备份根目录是否存在
if [ ! -d "${BACKUP_ROOT}" ]; then
    echo "错误: 备份根目录不存在: ${BACKUP_ROOT}，请先创建或修改 BACKUP_ROOT。"
    exit 1
fi

mkdir -p "${BACKUP_DIR}/schema" "${BACKUP_DIR}/data"
[ -z "${LOG_FILE}" ] && LOG_FILE="${BACKUP_DIR}/backup.log"
rotate_log_if_needed "${LOG_FILE}"
exec > >(tee -a "${LOG_FILE}") 2>&1

echo "[$(date '+%Y-%m-%d %H:%M:%S')] 开始备份数据库: ${DB_NAME}"
echo "[$(date '+%Y-%m-%d %H:%M:%S')] 日志文件: ${LOG_FILE}"

# 获取数据库中所有表的列表（用于后续按 -t / -i 过滤）
ALL_TABLES=$(${MYSQL_CMD} -e "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA='${DB_NAME}';" 2>/dev/null) || true

if [ -z "${ALL_TABLES}" ]; then
    echo "错误: 无法从数据库 '${DB_NAME}' 获取表列表。请检查配置。"
    exit 1
fi

# 根据 -t/-i 参数过滤需要备份的表：
# - 未指定 -t/-i 时：备份库中所有表
# - 指定 -t 时：仅备份 -t 列表中的表
# - 指定 -i 时：排除 -i 中的表；若同时指定 -t，则 -i 优先生效
TABLES=""

if [ -n "${TABLES_INCLUDE}" ]; then
    # 只选择包含列表中且真实存在的表
    for t in ${TABLES_INCLUDE}; do
        for at in ${ALL_TABLES}; do
            if [ "${t}" = "${at}" ]; then
                TABLES="${TABLES} ${t}"
                break
            fi
        done
    done
else
    TABLES="${ALL_TABLES}"
fi

if [ -n "${TABLES_EXCLUDE}" ]; then
    FILTERED=""
    for t in ${TABLES}; do
        SKIP=0
        for ex in ${TABLES_EXCLUDE}; do
            if [ "${t}" = "${ex}" ]; then
                SKIP=1
                break
            fi
        done
        [ "${SKIP}" -eq 1 ] && continue
        FILTERED="${FILTERED} ${t}"
    done
    TABLES="${FILTERED}"
fi

if [ -z "${TABLES}" ]; then
    echo "错误: 经过 -t/-i 过滤后，没有需要备份的表。"
    exit 1
fi

# 1. 备份每个表的结构 (不含数据)
echo "正在备份表结构..."
for TABLE in ${TABLES}; do
    echo "  -> 备份表结构: ${TABLE}"
    ${DUMP_CMD} --no-data "${DB_NAME}" "${TABLE}" > "${BACKUP_DIR}/schema/${TABLE}.sql"
done

# 2. 备份每个表的数据 (不含结构)，大表拆分为多个 sql
echo "正在备份表数据..."
for TABLE in ${TABLES}; do
    # 获取表行数（InnoDB 为估计值，用于判断是否拆分）
    ROW_COUNT=$(${MYSQL_CMD} -e "SELECT TABLE_ROWS FROM information_schema.TABLES WHERE TABLE_SCHEMA='${DB_NAME}' AND TABLE_NAME='${TABLE}';" 2>/dev/null) || echo "0"
    ROW_COUNT=${ROW_COUNT:-0}
    # 若无法获取或为 NULL，按大表处理以便拆分逻辑可用
    if [ -z "${ROW_COUNT}" ] || [ "${ROW_COUNT}" = "NULL" ]; then
        ROW_COUNT=$(${MYSQL_CMD} -e "SELECT COUNT(*) FROM \`${DB_NAME}\`.\`${TABLE}\`;" 2>/dev/null) || echo "0"
    fi

    if [ "${ROW_COUNT}" -le "${ROW_THRESHOLD}" ] 2>/dev/null; then
        # 小表：单文件
        echo "  -> 备份表数据: ${TABLE} (约 ${ROW_COUNT} 行, 单文件)"
        ${DUMP_CMD} --no-create-info "${DB_NAME}" "${TABLE}" > "${BACKUP_DIR}/data/${TABLE}.sql"
    else
        # 大表：尝试按主键范围拆分
        PK_COL=$(${MYSQL_CMD} -e "SELECT COLUMN_NAME FROM information_schema.KEY_COLUMN_USAGE \
            WHERE TABLE_SCHEMA='${DB_NAME}' AND TABLE_NAME='${TABLE}' AND CONSTRAINT_NAME='PRIMARY' \
            ORDER BY ORDINAL_POSITION LIMIT 1;" 2>/dev/null) || true

        if [ -n "${PK_COL}" ]; then
            MIN_MAX=$(${MYSQL_CMD} -e "SELECT MIN(\`${PK_COL}\`), MAX(\`${PK_COL}\`) FROM \`${DB_NAME}\`.\`${TABLE}\`;" 2>/dev/null) || true
            if [ -n "${MIN_MAX}" ]; then
                MIN_VAL=$(echo "${MIN_MAX}" | awk '{print $1}')
                MAX_VAL=$(echo "${MIN_MAX}" | awk '{print $2}')
                if [ -n "${MIN_VAL}" ] && [ -n "${MAX_VAL}" ] && [ "${MIN_VAL}" != "NULL" ] && [ "${MAX_VAL}" != "NULL" ]; then
                    PART=1
                    CURRENT=${MIN_VAL}
                    while [ "${CURRENT}" -le "${MAX_VAL}" ] 2>/dev/null; do
                        NEXT=$((CURRENT + CHUNK_SIZE))
                        WHERE="\`${PK_COL}\` >= ${CURRENT} AND \`${PK_COL}\` < ${NEXT}"
                        PAD=$(printf "%04d" "${PART}")
                        echo "  -> 备份表数据: ${TABLE} 第 ${PART} 部分 (${PK_COL} ${CURRENT}~${NEXT})"
                        ${DUMP_CMD} --no-create-info --where="${WHERE}" "${DB_NAME}" "${TABLE}" > "${BACKUP_DIR}/data/${TABLE}_${PAD}.sql"
                        PART=$((PART + 1))
                        CURRENT=${NEXT}
                    done
                    # 记录该表为按主键拆分的备份，便于恢复时按顺序执行
                    echo "${TABLE}_*.sql" > "${BACKUP_DIR}/data/.${TABLE}.split"
                    continue
                fi
            fi
        fi

        # 无可用主键或范围查询失败：使用 --extended-insert=false 按行写出，再用 split 按行数拆分
        echo "  -> 备份表数据: ${TABLE} (约 ${ROW_COUNT} 行, 按行拆分为多文件)"
        TEMP_FILE="${BACKUP_DIR}/data/${TABLE}.tmp.sql"
        ${DUMP_CMD} --no-create-info --extended-insert=false "${DB_NAME}" "${TABLE}" > "${TEMP_FILE}"
        # 按行数拆分：每 CHUNK_SIZE 行一个文件（每行一个 INSERT，避免截断）
        split -l "${CHUNK_SIZE}" -a 4 -d "${TEMP_FILE}" "${BACKUP_DIR}/data/${TABLE}_"
        for F in "${BACKUP_DIR}/data/${TABLE}_"[0-9]*; do
            [ -f "${F}" ] && mv "${F}" "${F}.sql"
        done
        rm -f "${TEMP_FILE}"
        echo "${TABLE}_*.sql" > "${BACKUP_DIR}/data/.${TABLE}.split"
    fi
done

echo "[$(date '+%Y-%m-%d %H:%M:%S')] 备份完成！"
echo "备份目录: ${BACKUP_DIR}"
echo "  - 表结构: ${BACKUP_DIR}/schema/"
echo "  - 表数据: ${BACKUP_DIR}/data/"
echo "  - 拆分的表会生成 .split 标记文件，恢复时需按顺序执行对应 *_*.sql"
echo "  - 日志: ${LOG_FILE}"

# 若指定了 -c，则清理指定天数前的旧备份
if [ -n "${CLEAN_DAYS}" ] && [ "${CLEAN_DAYS}" -gt 0 ] 2>/dev/null; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] 正在清理 ${CLEAN_DAYS} 天前的旧备份..."
    DELETED=0
    while IFS= read -r old_dir; do
        [ -z "${old_dir}" ] || [ ! -d "${old_dir}" ] && continue
        rm -rf "${old_dir}"
        echo "  -> 已删除: ${old_dir}"
        DELETED=$((DELETED + 1))
    done < <(find "${BACKUP_ROOT}" -maxdepth 1 -mindepth 1 -type d -mtime +"${CLEAN_DAYS}" 2>/dev/null)
    [ "${DELETED}" -eq 0 ] && echo "  (无满足条件的旧备份)"
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] 旧备份清理完成。"
fi
