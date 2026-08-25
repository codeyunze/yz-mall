package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.sys.dto.SaasTenantAddDto;
import com.yz.mall.sys.dto.SaasTenantDatasourceSaveDto;
import com.yz.mall.sys.dto.SaasTenantQueryDto;
import com.yz.mall.sys.dto.SaasTenantUpdateDto;
import com.yz.mall.sys.entity.SaasTenant;
import com.yz.mall.sys.entity.SaasTenantDatasource;
import com.yz.mall.sys.entity.SaasTenantDatasourceHistory;
import com.yz.mall.sys.entity.SaasTenantInitTask;
import com.yz.mall.sys.mapper.SaasTenantDatasourceHistoryMapper;
import com.yz.mall.sys.mapper.SaasTenantDatasourceMapper;
import com.yz.mall.sys.mapper.SaasTenantInitTaskMapper;
import com.yz.mall.sys.mapper.SaasTenantMapper;
import com.yz.mall.sys.service.SaasTenantSchemaMigrateService;
import com.yz.mall.sys.service.SaasTenantService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static com.yz.mall.sys.SysCoreConfig.ASYNC_EXECUTOR;

/**
 * SaaS-租户主表(SaasTenant)服务实现类
 *
 * @author yunze
 * @since 2026-04-20 00:00:00
 */
@Slf4j
@Service
public class SaasTenantServiceImpl extends ServiceImpl<SaasTenantMapper, SaasTenant> implements SaasTenantService {

    /**
     * 数据库名白名单：字母开头，后续仅允许字母/数字/下划线，长度2-64。
     */
    private static final Pattern DB_NAME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{1,63}$");

    @Resource
    private SaasTenantDatasourceHistoryMapper historyMapper;

    @Resource
    private SaasTenantDatasourceMapper datasourceMapper;

    @Resource
    private SaasTenantInitTaskMapper initTaskMapper;

    @Resource
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Resource
    private SaasTenantSchemaMigrateService schemaMigrateService;

    @PostConstruct
    public void loadAllDB() {
        LambdaQueryWrapper<SaasTenantDatasource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasTenantDatasource::getDsStatus, 1);
        List<SaasTenantDatasource> datasourceList = datasourceMapper.selectList(wrapper);
        for (SaasTenantDatasource datasource : datasourceList) {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setMinimumIdle(5);
            dataSource.setMaximumPoolSize(20);
            // 从池中获取连接的最大等待（对应原 Druid maxWait）
            dataSource.setConnectionTimeout(60000);
            // 空闲连接存活时间（对应原 minEvictableIdleTimeMillis）
            dataSource.setIdleTimeout(300000);
            dataSource.setMaxLifetime(1800000);
            dataSource.setKeepaliveTime(60000);
            dataSource.setConnectionTestQuery("SELECT 1");
            dataSource.setPoolName("tenant-" + datasource.getTenantCode());
            dataSource.setUsername(datasource.getDbUsername());
            dataSource.setPassword(datasource.getDbPasswordEnc());
            dataSource.setJdbcUrl("jdbc:mysql://" + datasource.getDbHost() + ":" + datasource.getDbPort() + "/" + datasource.getDbName() + "?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");

            dynamicRoutingDataSource.addDataSource(datasource.getTenantCode(), dataSource);

            log.info("======加载动态数据库完成：mysqlSchema={}", datasource.getDbName());
        }
        // 动态数据源加载完成后，对所有启用租户库执行 pending schema 迁移脚本
        schemaMigrateService.migrateAllOnStartup();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SaasTenantAddDto dto) {
        SaasTenant bo = new SaasTenant();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SaasTenantUpdateDto dto) {
        SaasTenant exists = baseMapper.selectById(dto.getId());
        if (exists == null) {
            return false;
        }

        SaasTenant bo = new SaasTenant();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SaasTenant> page(PageFilter<SaasTenantQueryDto> filter) {
        SaasTenantQueryDto query = filter.getFilter();
        LambdaQueryWrapper<SaasTenant> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            wrapper.like(query.getTenantCode() != null && !query.getTenantCode().isEmpty(),
                    SaasTenant::getTenantCode, query.getTenantCode());
            wrapper.like(query.getTenantName() != null && !query.getTenantName().isEmpty(),
                    SaasTenant::getTenantName, query.getTenantName());
            wrapper.eq(query.getTenantStatus() != null, SaasTenant::getTenantStatus, query.getTenantStatus());
        }
        wrapper.orderByDesc(SaasTenant::getCreateTime);
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), wrapper);
    }

    @Override
    public List<SaasTenant> list(SaasTenantQueryDto filter) {
        LambdaQueryWrapper<SaasTenant> wrapper = new LambdaQueryWrapper<>();
        if (filter != null) {
            wrapper.like(filter.getTenantCode() != null && !filter.getTenantCode().isEmpty(),
                    SaasTenant::getTenantCode, filter.getTenantCode());
            wrapper.like(filter.getTenantName() != null && !filter.getTenantName().isEmpty(),
                    SaasTenant::getTenantName, filter.getTenantName());
            wrapper.eq(filter.getTenantStatus() != null, SaasTenant::getTenantStatus, filter.getTenantStatus());
        }
        wrapper.orderByDesc(SaasTenant::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public SaasTenant getDetailById(Long tenantId, String serviceCode) {
        SaasTenant tenant = baseMapper.selectById(tenantId);
        if (tenant == null) {
            return null;
        }
        List<SaasTenant> tenants = new ArrayList<>();
        tenants.add(tenant);
        List<SaasTenant> filled = fillDatasourceFields(tenants, serviceCode);
        if (filled.isEmpty()) {
            tenant.setDbPasswordEnc("");
            return tenant;
        }
        return filled.get(0);
    }

    @Override
    public boolean initDatabase(Long tenantId) {
        return initDatabase(tenantId, null);
    }

    @Override
    public boolean initDatabase(Long tenantId, String serviceCode) {
        SaasTenant tenant = baseMapper.selectById(tenantId);
        if (tenant == null) {
            return false;
        }

        LambdaQueryWrapper<SaasTenantDatasource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasTenantDatasource::getTenantId, tenantId);
        wrapper.eq(serviceCode != null && !serviceCode.isBlank(), SaasTenantDatasource::getServiceCode, serviceCode);
        wrapper.eq(SaasTenantDatasource::getDsStatus, 1);
        wrapper.last("limit 1");
        SaasTenantDatasource datasource = datasourceMapper.selectOne(wrapper);
        if (datasource == null) {
            return false;
        }

        SaasTenantInitTask task = buildInitTask(tenantId, datasource.getTenantCode(), datasource.getServiceCode());
        task.setTaskStatus(1);
        task.setTaskType(1);
        task.setStepCode("CREATE_DATABASE");
        initTaskMapper.insert(task);

        try {
            createDatabase(datasource);
            task.setStepCode("INIT_SCHEMA");
            updateTask(task);
            executeInitScript(datasource);

            task.setStepCode("MIGRATE_SCHEMA");
            updateTask(task);
            if (!schemaMigrateService.migrateForDatasource(datasource)) {
                throw new IllegalStateException("租户 schema 增量迁移失败");
            }

            task.setTaskStatus(2);
            task.setStepCode("SUCCESS");
            task.setFinishedTime(java.time.LocalDateTime.now());
            updateTask(task);

            SaasTenant updateStatus = new SaasTenant();
            updateStatus.setId(tenantId);
            updateStatus.setTenantStatus(1);
            baseMapper.updateById(updateStatus);
            return true;
        } catch (Exception ex) {
            task.setTaskStatus(3);
            task.setStepCode("FAILED");
            task.setErrorMsg(ex.getMessage() == null ? "建库失败" : ex.getMessage());
            task.setFinishedTime(java.time.LocalDateTime.now());
            updateTask(task);
            return false;
        }
    }

    @Async(ASYNC_EXECUTOR)
    @Override
    public CompletableFuture<String> taskB() throws InterruptedException {
        // 随机睡眠1000~5000毫秒
        long sleepTime = (long) (Math.random() * 4000 + 1000);
        Thread.sleep(sleepTime);
        return CompletableFuture.completedFuture("taskB: " + sleepTime);
    }

    @Override
    public List<SaasTenantDatasource> listDatasource(Long tenantId) {
        LambdaQueryWrapper<SaasTenantDatasource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasTenantDatasource::getTenantId, tenantId);
        wrapper.orderByDesc(SaasTenantDatasource::getUpdateTime);
        List<SaasTenantDatasource> datasourceList = datasourceMapper.selectList(wrapper);
        datasourceList.forEach(item -> item.setDbPasswordEnc(""));
        return datasourceList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateDatasource(SaasTenantDatasourceSaveDto dto) {
        validateDbName(dto.getDbName());
        SaasTenant tenant = baseMapper.selectById(dto.getTenantId());
        if (tenant == null) {
            throw new IllegalArgumentException("租户不存在");
        }
        LambdaQueryWrapper<SaasTenantDatasource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasTenantDatasource::getTenantId, dto.getTenantId());
        wrapper.eq(SaasTenantDatasource::getServiceCode, dto.getServiceCode());
        SaasTenantDatasource exists = dto.getId() == null ? datasourceMapper.selectOne(wrapper) : datasourceMapper.selectById(dto.getId());

        SaasTenantDatasource datasource = new SaasTenantDatasource();
        datasource.setId(exists == null ? IdUtil.getSnowflakeNextId() : exists.getId());
        datasource.setTenantId(tenant.getId());
        datasource.setTenantCode(tenant.getTenantCode());
        datasource.setServiceCode(dto.getServiceCode());
        datasource.setDbType(dto.getDbType());
        datasource.setDbHost(dto.getDbHost());
        datasource.setDbPort(dto.getDbPort());
        datasource.setDbName(dto.getDbName());
        datasource.setDbUsername(dto.getDbUsername());
        datasource.setDbParams(dto.getDbParams());
        datasource.setRemark(dto.getRemark());
        datasource.setDsStatus(dto.getDsStatus() == null ? 1 : dto.getDsStatus());

        if (exists != null && (dto.getDbPasswordEnc() == null || dto.getDbPasswordEnc().isBlank())) {
            datasource.setDbPasswordEnc(exists.getDbPasswordEnc());
        } else {
            datasource.setDbPasswordEnc(dto.getDbPasswordEnc());
        }

        if (exists == null) {
            datasourceMapper.insert(datasource);
            saveHistory(datasource, "租户数据源新增");
        } else {
            boolean changed = dbConfigChanged(exists, datasource);
            datasourceMapper.updateById(datasource);
            if (changed) {
                historyMapper.update(
                        null,
                        new LambdaUpdateWrapper<SaasTenantDatasourceHistory>()
                                .eq(SaasTenantDatasourceHistory::getTenantId, datasource.getTenantId())
                                .eq(SaasTenantDatasourceHistory::getServiceCode, datasource.getServiceCode())
                                .eq(SaasTenantDatasourceHistory::getIsCurrent, 1)
                                .set(SaasTenantDatasourceHistory::getIsCurrent, 0)
                );
                saveHistory(datasource, "租户数据库连接更新");
            }
        }
        return datasource.getId();
    }

    @Override
    public boolean removeDatasource(Long id) {
        return datasourceMapper.deleteById(id) > 0;
    }

    /**
     * 保存租户数据库配置历史快照
     *
     * @param datasource
     * @param reason 变更原因
     */
    private void saveHistory(SaasTenantDatasource datasource, String reason) {
        SaasTenantDatasourceHistory history = new SaasTenantDatasourceHistory();
        history.setId(IdUtil.getSnowflakeNextId());
        history.setTenantId(datasource.getTenantId());
        history.setTenantCode(datasource.getTenantCode());
        history.setServiceCode(datasource.getServiceCode());
        history.setDbType(datasource.getDbType());
        history.setDbHost(datasource.getDbHost());
        history.setDbPort(datasource.getDbPort());
        history.setDbName(datasource.getDbName());
        history.setDbUsername(datasource.getDbUsername());
        history.setDbPasswordEnc(datasource.getDbPasswordEnc());
        history.setDbParams(datasource.getDbParams());
        history.setIsCurrent(1);
        history.setChangeReason(reason);
        historyMapper.insert(history);
    }

    /**
     * 构建初始化任务基础信息
     *
     * @param tenantId 租户ID
     * @return 初始化任务对象
     */
    private SaasTenantInitTask buildInitTask(Long tenantId, String tenantCode, String serviceCode) {
        SaasTenantInitTask task = new SaasTenantInitTask();
        task.setId(IdUtil.getSnowflakeNextId());
        task.setTenantId(tenantId);
        task.setTenantCode(tenantCode);
        task.setServiceCode(serviceCode);
        task.setTaskNo(String.valueOf(IdUtil.getSnowflakeNextId()));
        task.setRetryCount(0);
        return task;
    }

    /**
     * 创建租户数据库
     *
     * @param datasource 租户信息
     * @throws SQLException 数据库连接或执行异常
     */
    private void createDatabase(SaasTenantDatasource datasource) throws SQLException {
        // 先连接到 MySQL Server（不指定具体业务库），再执行建库语句
        String serverJdbcUrl = "jdbc:mysql://" + datasource.getDbHost() + ":" + datasource.getDbPort()
                + "/?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&serverTimezone=Asia/Shanghai";
        try (Connection connection = DriverManager.getConnection(
                serverJdbcUrl, datasource.getDbUsername(), datasource.getDbPasswordEnc())) {
            String sql = "CREATE DATABASE IF NOT EXISTS `" + datasource.getDbName()
                    + "` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci";
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }

    /**
     * 执行租户数据库初始化脚本
     *
     * @param datasource 租户信息
     * @throws SQLException 数据库连接或执行异常
     */
    private void executeInitScript(SaasTenantDatasource datasource) throws SQLException {
        String jdbcUrl = buildTenantJdbcUrl(datasource);
        try (Connection connection = DriverManager.getConnection(
                jdbcUrl, datasource.getDbUsername(), datasource.getDbPasswordEnc())) {
            // 初始化脚本放在 classpath:sql/tenant-init.sql
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/tenant-init.sql"));
        }
    }

    /**
     * 构建租户数据库 JDBC 连接串
     *
     * @param datasource 租户信息
     * @return JDBC URL
     */
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

    /**
     * 更新初始化任务状态
     *
     * @param task 初始化任务
     */
    private void updateTask(SaasTenantInitTask task) {
        initTaskMapper.updateById(task);
    }

    /**
     * 用租户数据源信息填充租户展示对象（不回填密码）。
     *
     * @param tenants     租户列表
     * @param serviceCode 可选服务标识过滤
     * @return 填充后的租户列表
     */
    private List<SaasTenant> fillDatasourceFields(List<SaasTenant> tenants, String serviceCode) {
        if (tenants == null || tenants.isEmpty()) {
            return tenants;
        }
        List<SaasTenant> result = new ArrayList<>();
        for (SaasTenant tenant : tenants) {
            LambdaQueryWrapper<SaasTenantDatasource> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SaasTenantDatasource::getTenantId, tenant.getId());
            wrapper.eq(serviceCode != null && !serviceCode.isBlank(), SaasTenantDatasource::getServiceCode, serviceCode);
            wrapper.eq(SaasTenantDatasource::getDsStatus, 1);
            wrapper.orderByDesc(SaasTenantDatasource::getUpdateTime);
            wrapper.last("limit 1");
            SaasTenantDatasource datasource = datasourceMapper.selectOne(wrapper);
            if (datasource == null) {
                continue;
            }
            tenant.setServiceCode(datasource.getServiceCode());
            tenant.setDbType(datasource.getDbType());
            tenant.setDbHost(datasource.getDbHost());
            tenant.setDbPort(datasource.getDbPort());
            tenant.setDbName(datasource.getDbName());
            tenant.setDbUsername(datasource.getDbUsername());
            tenant.setDbParams(datasource.getDbParams());
            tenant.setDsStatus(datasource.getDsStatus());
            tenant.setDbPasswordEnc("");
            result.add(tenant);
        }
        return result;
    }

    /**
     * 校验数据库名合法性，防止特殊字符导致 SQL 注入风险。
     *
     * @param dbName 数据库名
     */
    private void validateDbName(String dbName) {
        if (dbName == null || dbName.isBlank() || !DB_NAME_PATTERN.matcher(dbName).matches()) {
            throw new IllegalArgumentException("数据库名不合法，仅允许字母开头，且包含字母数字下划线，长度2-64");
        }
    }

    /**
     * 判断数据库连接配置是否发生变更
     *
     * @param before 变更前租户配置
     * @param after  变更后租户配置
     * @return true 已变更，false 未变更
     */
    private boolean dbConfigChanged(SaasTenantDatasource before, SaasTenantDatasource after) {
        return !Objects.equals(before.getDbType(), after.getDbType())
                || !Objects.equals(before.getDbHost(), after.getDbHost())
                || !Objects.equals(before.getDbPort(), after.getDbPort())
                || !Objects.equals(before.getDbName(), after.getDbName())
                || !Objects.equals(before.getDbUsername(), after.getDbUsername())
                || !Objects.equals(before.getDbPasswordEnc(), after.getDbPasswordEnc())
                || !Objects.equals(before.getDbParams(), after.getDbParams())
                || !Objects.equals(before.getDsStatus(), after.getDsStatus());
    }

}
