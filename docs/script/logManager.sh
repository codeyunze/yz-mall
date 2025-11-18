#!/bin/bash

# nohup日志管理脚本
LOG_FILE="nohup.out"
BACKUP_DIR="./log_backups"
MAX_SIZE="100M"
MAX_BACKUPS=30

usage() {
    echo "用法: $0 [选项]"
    echo "选项:"
    echo "  -c, --clean     清空日志文件"
    echo "  -b, --backup    备份并清空日志"
    echo "  -s, --size      按大小清理（默认）"
    echo "  -v, --view      查看日志大小"
    echo "  -h, --help      显示帮助信息"
}

view_size() {
    if [ -f "$LOG_FILE" ]; then
        size=$(du -h "$LOG_FILE" | cut -f1)
        lines=$(wc -l < "$LOG_FILE" 2>/dev/null || echo "0")
        echo "日志文件: $LOG_FILE"
        echo "文件大小: $size"
        echo "行数: $lines"
    else
        echo "日志文件不存在"
    fi
}

clean_log() {
    if [ -f "$LOG_FILE" ]; then
        > "$LOG_FILE"
        echo "已清空日志文件"
    else
        echo "日志文件不存在"
    fi
}

backup_and_clean() {
    mkdir -p "$BACKUP_DIR"
    if [ -f "$LOG_FILE" ] && [ -s "$LOG_FILE" ]; then
        backup_file="$BACKUP_DIR/nohup_$(date +%Y%m%d_%H%M%S).log"
        cp "$LOG_FILE" "$backup_file"
        > "$LOG_FILE"
        echo "已备份到 $backup_file 并清空原文件"

        # 清理旧备份
        ls -t "$BACKUP_DIR"/nohup_*.log 2>/dev/null | tail -n +$(($MAX_BACKUPS + 1)) | xargs rm -f 2>/dev/null
    else
        echo "日志文件为空或不存在"
    fi
}


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

clean_by_size() {
    if [ ! -f "$LOG_FILE" ]; then
        echo "日志文件不存在"
        return
    fi

    FILE_SIZE=$(du -b "$LOG_FILE" | cut -f1)
    MAX_SIZE_BYTES=$(convert_to_bytes "$MAX_SIZE")

    if [ "$FILE_SIZE" -gt "$MAX_SIZE_BYTES" ]; then
        backup_and_clean
    else
        echo "日志大小未超过限制 ($MAX_SIZE)，无需清理"
    fi
}

# 解析参数
case "${1:-}" in
    -c|--clean)
        clean_log
        ;;
    -b|--backup)
        backup_and_clean
        ;;
    -s|--size)
        clean_by_size
        ;;
    -v|--view)
        view_size
        ;;
    -h|--help)
        usage
        ;;
    *)
        echo "自动检测模式..."
        clean_by_size
        ;;
esac