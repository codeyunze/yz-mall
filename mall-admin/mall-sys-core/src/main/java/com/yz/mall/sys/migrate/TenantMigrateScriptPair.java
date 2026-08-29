package com.yz.mall.sys.migrate;

import org.springframework.core.io.Resource;

/**
 * 一对正/回滚迁移脚本。
 *
 * @param serviceCode 服务标识（目录名）
 * @param scriptName 正向脚本文件名
 * @param forward 正向脚本资源
 * @param rollback 回滚脚本资源
 * @param checksum 正向脚本校验值
 * @author yunze
 * @since 2026-08-25
 */
public record TenantMigrateScriptPair(
        String serviceCode,
        String scriptName,
        Resource forward,
        Resource rollback,
        String checksum
) {
}
