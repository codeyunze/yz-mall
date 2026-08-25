package com.yz.mall.sys.service;

import com.yz.mall.sys.entity.SaasTenantDatasource;

/**
 * 租户库 schema 增量迁移：扫描 resources 脚本并对启用数据源执行，失败则回滚本轮已成功项。
 *
 * @author yunze
 * @since 2026-08-25
 */
public interface SaasTenantSchemaMigrateService {

    /**
     * 启动时：对所有启用数据源按 serviceCode 执行 pending 正向脚本；任一失败则逆序回滚本轮全部成功项并中止。
     */
    void migrateAllOnStartup();

    /**
     * 新租户初始化后：对该数据源执行其 serviceCode 下全部 pending 脚本；失败则回滚本轮已执行项。
     *
     * @param datasource 租户数据源
     * @return true 全部成功；false 失败（已尝试回滚）
     */
    boolean migrateForDatasource(SaasTenantDatasource datasource);
}
