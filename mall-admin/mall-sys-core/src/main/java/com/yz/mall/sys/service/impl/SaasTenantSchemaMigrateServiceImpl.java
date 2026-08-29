package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yz.mall.sys.entity.SaasSchemaMigrateLog;
import com.yz.mall.sys.entity.SaasTenantDatasource;
import com.yz.mall.sys.mapper.SaasSchemaMigrateLogMapper;
import com.yz.mall.sys.mapper.SaasTenantDatasourceMapper;
import com.yz.mall.sys.migrate.TenantMigrateScriptPair;
import com.yz.mall.sys.migrate.TenantMigrateScriptScanner;
import com.yz.mall.sys.service.SaasTenantSchemaMigrateService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * 租户库 schema 增量迁移实现：启动全量 / 单租户补跑，失败则逆序回滚本轮已成功项。
 *
 * @author yunze
 * @since 2026-08-25
 */
@Slf4j
@Service
public class SaasTenantSchemaMigrateServiceImpl implements SaasTenantSchemaMigrateService {

    /**
     * 执行状态：成功
     */
    public static final int STATUS_SUCCESS = 1;
    /**
     * 执行状态：失败
     */
    public static final int STATUS_FAILED = 2;
    /**
     * 执行状态：已回滚
     */
    public static final int STATUS_ROLLED_BACK = 3;

    @Resource
    private SaasTenantDatasourceMapper datasourceMapper;

    @Resource
    private SaasSchemaMigrateLogMapper migrateLogMapper;

    @Override
    public void migrateAllOnStartup() {
        Map<String, List<TenantMigrateScriptPair>> scriptsByService;
        try {
            scriptsByService = TenantMigrateScriptScanner.scanAll();
        } catch (Exception ex) {
            throw new IllegalStateException("扫描租户迁移脚本失败: " + ex.getMessage(), ex);
        }
        if (scriptsByService.isEmpty()) {
            log.info("======租户 schema 迁移：无待执行脚本目录，跳过");
            return;
        }

        Deque<AppliedMigration> appliedStack = new ArrayDeque<>();
        try {
            // common 目录优先于各 serviceCode，保证公共表先落地
            List<Map.Entry<String, List<TenantMigrateScriptPair>>> orderedEntries = new ArrayList<>(scriptsByService.entrySet());
            orderedEntries.sort((a, b) -> {
                if (TenantMigrateScriptScanner.isCommonDir(a.getKey())) {
                    return -1;
                }
                if (TenantMigrateScriptScanner.isCommonDir(b.getKey())) {
                    return 1;
                }
                return a.getKey().compareTo(b.getKey());
            });
            for (Map.Entry<String, List<TenantMigrateScriptPair>> entry : orderedEntries) {
                String scriptDir = entry.getKey();
                List<SaasTenantDatasource> datasources = listTargetDatasources(scriptDir);
                if (datasources.isEmpty()) {
                    log.info("======租户 schema 迁移：scriptDir={} 无启用数据源，跳过", scriptDir);
                    continue;
                }
                for (TenantMigrateScriptPair pair : entry.getValue()) {
                    for (SaasTenantDatasource datasource : datasources) {
                        if (alreadySuccess(datasource, pair)) {
                            continue;
                        }
                        try {
                            executeScript(datasource, pair.forward());
                            SaasSchemaMigrateLog successLog = insertLog(datasource, pair, STATUS_SUCCESS, null, LocalDateTime.now(), null);
                            appliedStack.push(new AppliedMigration(datasource, pair, successLog.getId()));
                            log.info("======租户 schema 迁移成功：tenantCode={}, dsService={}, scriptDir={}, script={}",
                                    datasource.getTenantCode(), datasource.getServiceCode(), scriptDir, pair.scriptName());
                        } catch (Exception ex) {
                            String err = ex.getMessage() == null ? "执行失败" : ex.getMessage();
                            insertLog(datasource, pair, STATUS_FAILED, err, LocalDateTime.now(), null);
                            log.error("======租户 schema 迁移失败，开始本轮全部回滚：tenantCode={}, dsService={}, scriptDir={}, script={}, err={}",
                                    datasource.getTenantCode(), datasource.getServiceCode(), scriptDir, pair.scriptName(), err, ex);
                            rollbackAll(appliedStack, err);
                            throw new IllegalStateException("租户 schema 迁移失败并已回滚本轮成功项: scriptDir="
                                    + scriptDir + ", script=" + pair.scriptName() + ", tenant=" + datasource.getTenantCode()
                                    + ", cause=" + err, ex);
                        }
                    }
                }
            }
            log.info("======租户 schema 迁移全部完成");
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            rollbackAll(appliedStack, ex.getMessage());
            throw new IllegalStateException("租户 schema 迁移异常并已回滚本轮成功项: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean migrateForDatasource(SaasTenantDatasource datasource) {
        if (datasource == null || datasource.getServiceCode() == null || datasource.getServiceCode().isBlank()) {
            return false;
        }
        List<TenantMigrateScriptPair> pairs;
        try {
            pairs = TenantMigrateScriptScanner.scanApplicableScripts(datasource.getServiceCode());
        } catch (Exception ex) {
            log.error("扫描租户迁移脚本失败: serviceCode={}, err={}", datasource.getServiceCode(), ex.getMessage(), ex);
            return false;
        }
        if (pairs.isEmpty()) {
            return true;
        }

        Deque<AppliedMigration> appliedStack = new ArrayDeque<>();
        for (TenantMigrateScriptPair pair : pairs) {
            if (alreadySuccess(datasource, pair)) {
                continue;
            }
            try {
                executeScript(datasource, pair.forward());
                SaasSchemaMigrateLog successLog = insertLog(datasource, pair, STATUS_SUCCESS, null, LocalDateTime.now(), null);
                appliedStack.push(new AppliedMigration(datasource, pair, successLog.getId()));
            } catch (Exception ex) {
                String err = ex.getMessage() == null ? "执行失败" : ex.getMessage();
                insertLog(datasource, pair, STATUS_FAILED, err, LocalDateTime.now(), null);
                log.error("单租户 schema 迁移失败，回滚本轮：tenantCode={}, script={}, err={}",
                        datasource.getTenantCode(), TenantMigrateScriptScanner.toLogScriptName(pair), err, ex);
                rollbackAll(appliedStack, err);
                return false;
            }
        }
        return true;
    }

    /**
     * common 目录 → 全部启用数据源；其他目录 → 仅匹配 serviceCode 的启用数据源。
     */
    private List<SaasTenantDatasource> listTargetDatasources(String scriptDir) {
        if (TenantMigrateScriptScanner.isCommonDir(scriptDir)) {
            LambdaQueryWrapper<SaasTenantDatasource> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SaasTenantDatasource::getDsStatus, 1);
            wrapper.orderByAsc(SaasTenantDatasource::getServiceCode).orderByAsc(SaasTenantDatasource::getTenantCode);
            return datasourceMapper.selectList(wrapper);
        }
        return listEnabledByService(scriptDir);
    }

    private List<SaasTenantDatasource> listEnabledByService(String serviceCode) {
        LambdaQueryWrapper<SaasTenantDatasource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasTenantDatasource::getServiceCode, serviceCode);
        wrapper.eq(SaasTenantDatasource::getDsStatus, 1);
        wrapper.orderByAsc(SaasTenantDatasource::getTenantCode);
        return datasourceMapper.selectList(wrapper);
    }

    private boolean alreadySuccess(SaasTenantDatasource datasource, TenantMigrateScriptPair pair) {
        LambdaQueryWrapper<SaasSchemaMigrateLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasSchemaMigrateLog::getTenantCode, datasource.getTenantCode());
        // 日志按「实际数据源服务」区分；common 脚本名带 common/ 前缀
        wrapper.eq(SaasSchemaMigrateLog::getServiceCode, datasource.getServiceCode());
        wrapper.eq(SaasSchemaMigrateLog::getScriptName, TenantMigrateScriptScanner.toLogScriptName(pair));
        wrapper.eq(SaasSchemaMigrateLog::getExecStatus, STATUS_SUCCESS);
        wrapper.last("limit 1");
        return migrateLogMapper.selectOne(wrapper) != null;
    }

    private void executeScript(SaasTenantDatasource datasource, org.springframework.core.io.Resource script) throws SQLException {
        String jdbcUrl = buildTenantJdbcUrl(datasource);
        try (Connection connection = DriverManager.getConnection(
                jdbcUrl, datasource.getDbUsername(), datasource.getDbPasswordEnc())) {
            ScriptUtils.executeSqlScript(connection, script);
        }
    }

    private SaasSchemaMigrateLog insertLog(SaasTenantDatasource datasource, TenantMigrateScriptPair pair,
                                           int status, String errorMsg, LocalDateTime execTime, LocalDateTime rollbackTime) {
        SaasSchemaMigrateLog logEntity = new SaasSchemaMigrateLog();
        logEntity.setId(IdUtil.getSnowflakeNextId());
        logEntity.setTenantId(datasource.getTenantId());
        logEntity.setTenantCode(datasource.getTenantCode());
        logEntity.setServiceCode(datasource.getServiceCode());
        logEntity.setScriptName(TenantMigrateScriptScanner.toLogScriptName(pair));
        logEntity.setScriptChecksum(pair.checksum());
        logEntity.setExecStatus(status);
        logEntity.setErrorMsg(truncate(errorMsg, 1000));
        logEntity.setExecTime(execTime);
        logEntity.setRollbackTime(rollbackTime);
        migrateLogMapper.insert(logEntity);
        return logEntity;
    }

    private void rollbackAll(Deque<AppliedMigration> appliedStack, String reason) {
        List<AppliedMigration> snapshot = new ArrayList<>(appliedStack);
        while (!appliedStack.isEmpty()) {
            AppliedMigration applied = appliedStack.pop();
            try {
                executeScript(applied.datasource(), applied.pair().rollback());
                markRolledBack(applied.logId(), reason);
                log.warn("======租户 schema 回滚成功：tenantCode={}, serviceCode={}, script={}",
                        applied.datasource().getTenantCode(), applied.pair().serviceCode(), applied.pair().scriptName());
            } catch (Exception ex) {
                String err = "回滚失败: " + (ex.getMessage() == null ? reason : ex.getMessage());
                markRolledBack(applied.logId(), err);
                log.error("======租户 schema 回滚失败（需人工介入）：tenantCode={}, script={}, err={}",
                        applied.datasource().getTenantCode(), applied.pair().scriptName(), err, ex);
            }
        }
        if (!snapshot.isEmpty()) {
            log.error("======本轮租户 schema 迁移已回滚 {} 项，原因：{}", snapshot.size(), reason);
        }
    }

    private void markRolledBack(Long logId, String errorMsg) {
        SaasSchemaMigrateLog update = new SaasSchemaMigrateLog();
        update.setId(logId);
        update.setExecStatus(STATUS_ROLLED_BACK);
        update.setErrorMsg(truncate(errorMsg, 1000));
        update.setRollbackTime(LocalDateTime.now());
        migrateLogMapper.updateById(update);
    }

    private String buildTenantJdbcUrl(SaasTenantDatasource datasource) {
        StringBuilder urlBuilder = new StringBuilder("jdbc:mysql://")
                .append(datasource.getDbHost())
                .append(":")
                .append(datasource.getDbPort())
                .append("/")
                .append(datasource.getDbName());
        if (datasource.getDbParams() != null && !datasource.getDbParams().isBlank()) {
            String params = datasource.getDbParams().startsWith("?")
                    ? datasource.getDbParams().substring(1)
                    : datasource.getDbParams();
            urlBuilder.append("?").append(params);
        } else {
            urlBuilder.append("?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&serverTimezone=Asia/Shanghai");
        }
        return urlBuilder.toString();
    }

    private static String truncate(String text, int max) {
        if (text == null) {
            return null;
        }
        return text.length() <= max ? text : text.substring(0, max);
    }

    /**
     * 本轮已成功应用的迁移项（用于失败时逆序回滚）。
     */
    private record AppliedMigration(SaasTenantDatasource datasource, TenantMigrateScriptPair pair, Long logId) {
    }
}
