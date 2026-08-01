package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.sys.dto.SaasTenantAddDto;
import com.yz.mall.sys.dto.SaasTenantDatasourceSaveDto;
import com.yz.mall.sys.dto.SaasTenantQueryDto;
import com.yz.mall.sys.dto.SaasTenantUpdateDto;
import com.yz.mall.sys.entity.SaasTenant;
import com.yz.mall.sys.entity.SaasTenantDatasource;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * SaaS-租户主表(SaasTenant)服务接口
 *
 * @author yunze
 * @since 2026-04-20 00:00:00
 */
public interface SaasTenantService extends IService<SaasTenant> {

    Long save(SaasTenantAddDto dto);

    boolean update(@Valid SaasTenantUpdateDto dto);

    Page<SaasTenant> page(PageFilter<SaasTenantQueryDto> filter);

    List<SaasTenant> list(SaasTenantQueryDto filter);

    /**
     * 查询租户详情（包含服务数据源信息，密码不回显）
     *
     * @param tenantId    租户ID
     * @param serviceCode 服务标识，可为空
     * @return 租户详情
     */
    SaasTenant getDetailById(Long tenantId, String serviceCode);

    /**
     * 查询租户下服务数据源列表
     *
     * @param tenantId 租户ID
     * @return 数据源列表
     */
    List<SaasTenantDatasource> listDatasource(Long tenantId);

    /**
     * 新增或更新租户服务数据源配置
     *
     * @param dto 数据源配置
     * @return 主键ID
     */
    Long saveOrUpdateDatasource(SaasTenantDatasourceSaveDto dto);

    /**
     * 删除租户服务数据源配置
     *
     * @param id 数据源ID
     * @return 是否成功
     */
    boolean removeDatasource(Long id);

    /**
     * 创建租户数据库并执行初始化脚本
     *
     * @param tenantId 租户ID
     * @return 是否执行成功
     */
    boolean initDatabase(Long tenantId);

    /**
     * 按服务标识创建租户数据库并执行初始化脚本
     *
     * @param tenantId    租户ID
     * @param serviceCode 服务标识
     * @return 是否执行成功
     */
    boolean initDatabase(Long tenantId, String serviceCode);

    public CompletableFuture<String> taskB() throws InterruptedException;
}
