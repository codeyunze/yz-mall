#!/bin/bash

# -f / --force: 若服务已运行则直接停止，不提示确认
# -d / --debug: 启用 Java 远程调试（端口为 app_port + 1000）
FORCE_STOP=0
ENABLE_DEBUG=0
for arg in "$@"; do
    case "$arg" in
        -f|--force) FORCE_STOP=1 ;;
        -d|--debug) ENABLE_DEBUG=1 ;;
    esac
done

# ============ 应用配置 ============
app_name="yz-unqid-startup"
app_version="0.0.1-SNAPSHOT"
app_port="30008"
app_active=test
java_opts="-Xms1536m -Xmx1536m -Xmn512m -Xss512K -XX:+UseG1GC -Xlog:gc*:file=./gc.log:time,uptime,level,tags:filecount=5,filesize=10M -XX:HeapDumpPath=./heapdump.hprof -XX:+HeapDumpOnOutOfMemoryError"
# 调试参数：仅 -d/--debug 时生效
[ "$ENABLE_DEBUG" -eq 1 ] && java_debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$((app_port + 1000))" || java_debug=""

# ============ 日志管理配置 ============
LOG_FILE="console.log"
BACKUP_DIR="./log_backups"
MAX_SIZE="100M"
MAX_BACKUPS=30
CHECK_INTERVAL=300   # 检查间隔（秒），默认 5 分钟

# 将大小字符串转为字节数
convert_to_bytes() {
    local size=$1
    local bytes
    case "$size" in
        *G) bytes=$(( ${size%G} * 1024 * 1024 * 1024 )) ;;
        *M) bytes=$(( ${size%M} * 1024 * 1024 )) ;;
        *K) bytes=$(( ${size%K} * 1024 )) ;;
        *)  bytes=$size ;;
    esac
    echo $bytes
}

# 备份并清空日志
backup_and_clean() {
    mkdir -p "$BACKUP_DIR"
    if [ -f "$LOG_FILE" ] && [ -s "$LOG_FILE" ]; then
        backup_file="$BACKUP_DIR/console_$(date +%Y%m%d_%H%M%S).log"
        cp "$LOG_FILE" "$backup_file"
        > "$LOG_FILE"
        echo "[$(date '+%Y-%m-%d %H:%M:%S')] 已备份到 $backup_file 并清空原文件"

        # 清理旧备份，只保留最近 MAX_BACKUPS 个
        ls -t "$BACKUP_DIR"/console_*.log 2>/dev/null | tail -n +$(($MAX_BACKUPS + 1)) | xargs rm -f 2>/dev/null
    fi
}

# 按大小检查并轮转：超过 MAX_SIZE 时备份并清空
clean_by_size() {
    if [ ! -f "$LOG_FILE" ]; then
        return
    fi

    FILE_SIZE=$(wc -c < "$LOG_FILE" 2>/dev/null || echo 0)
    MAX_SIZE_BYTES=$(convert_to_bytes "$MAX_SIZE")

    if [ "$FILE_SIZE" -gt "$MAX_SIZE_BYTES" ]; then
        backup_and_clean
    fi
}

# 后台日志轮转循环：定期检查，当主进程退出时停止
log_rotate_loop() {
    local main_pid=$1
    while kill -0 $main_pid 2>/dev/null; do
        sleep $CHECK_INTERVAL
        clean_by_size
    done
}

# 获取占用 app_port 的进程 PID 列表
get_running_pids() {
    # lsof 兼容 macOS 与 Linux
    lsof -t -i ":$app_port" 2>/dev/null
}

# 停止正在运行的服务
stop_running_service() {
    local pids
    pids=$(get_running_pids)
    if [ -z "$pids" ]; then
        return 0
    fi

    echo "正在停止服务 (PID: $pids)..."
    for pid in $pids; do
        kill -TERM "$pid" 2>/dev/null
    done

    # 等待进程退出，最多 30 秒
    local wait_count=0
    while [ $wait_count -lt 30 ]; do
        pids=$(get_running_pids)
        if [ -z "$pids" ]; then
            echo "服务已停止"
            return 0
        fi
        sleep 1
        wait_count=$((wait_count + 1))
    done

    # 超时则强制 kill
    echo "未能在 30 秒内退出，强制终止..."
    pids=$(get_running_pids)
    for pid in $pids; do
        kill -9 "$pid" 2>/dev/null
    done
    sleep 1
    echo "服务已强制停止"
}

# 检查服务是否运行，若运行则提示并处理
check_and_handle_running() {
    local pids
    pids=$(get_running_pids)
    if [ -z "$pids" ]; then
        return 0
    fi

    echo "服务正在运行 (端口: $app_port, PID: $pids)"

    if [ "$FORCE_STOP" -eq 1 ]; then
        echo "使用 -f 参数，直接停止已运行的服务"
        stop_running_service
        return 0
    fi

    echo -n "是否停止正在运行的服务？(y/n): "
    read -r answer

    case "$answer" in
        [yY]|[yY][eE][sS])
            stop_running_service
            return 0
            ;;
        *)
            echo "已取消启动"
            exit 1
            ;;
    esac
}

# 运行前检查：若服务已在运行则提示并处理
check_and_handle_running

# 启动前检查并轮转现有日志（若上次运行的日志已超限）
clean_by_size

# 启动应用
nohup java -server -Duser.timezone=Asia/Shanghai -jar $java_opts $java_debug ${app_name}-${app_version}.jar \
    --spring.profiles.active=$app_active --server.port=$app_port --spring.application.name=$app_name \
    >> "$LOG_FILE" 2>&1 &

JAVA_PID=$!

# 启动后台日志轮转进程（随 Java 进程退出而退出）
log_rotate_loop $JAVA_PID &
ROTATE_PID=$!

echo "${app_name}-${app_version} 服务启动成功"
echo "  日志文件: $LOG_FILE"
echo "  日志备份: $BACKUP_DIR (超过 $MAX_SIZE 时自动备份，保留最近 $MAX_BACKUPS 个)"
[ "$ENABLE_DEBUG" -eq 1 ] && echo "  调试端口: $((app_port + 1000)) (已启用 -d)"
echo "  进程 PID: $JAVA_PID"
