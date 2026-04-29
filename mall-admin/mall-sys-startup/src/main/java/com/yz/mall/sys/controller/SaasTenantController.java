package com.yz.mall.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.sys.dto.SaasTenantAddDto;
import com.yz.mall.sys.dto.SaasTenantDatasourceSaveDto;
import com.yz.mall.sys.dto.SaasTenantQueryDto;
import com.yz.mall.sys.dto.SaasTenantUpdateDto;
import com.yz.mall.sys.entity.SaasTenant;
import com.yz.mall.sys.entity.SaasTenantDatasource;
import com.yz.mall.sys.service.SaasTenantService;
import com.yz.mall.web.annotation.RepeatSubmit;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

/**
 * SaaS-租户管理
 *
 * @author yunze
 * @since 2026-04-20 00:00:00
 */
@RestController
@RequestMapping("sys/tenant")
public class SaasTenantController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private SaasTenantService service;

    /**
     * 新增租户
     */
    @RepeatSubmit
    @SaCheckPermission("api:system:tenant:edit")
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SaasTenantAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新租户
     */
    @RepeatSubmit
    @SaCheckPermission("api:system:tenant:edit")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SaasTenantUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除租户
     *
     * @param id 租户主键ID
     */
    @RepeatSubmit
    @SaCheckPermission("api:system:tenant:edit")
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 创建租户数据库并执行初始化脚本
     *
     * @param id 租户主键ID
     */
    @RepeatSubmit
    @SaCheckPermission("api:system:tenant:edit")
    @PostMapping("initDb/{id}")
    public Result<Boolean> initDb(@PathVariable Long id,
                                  @RequestParam(required = false) String serviceCode) {
        return success(this.service.initDatabase(id, serviceCode));
    }

    /**
     * 分页查询租户
     */
    @SaCheckPermission("api:system:tenant:list")
    @PostMapping("page")
    public Result<ResultTable<SaasTenant>> page(@RequestBody @Valid PageFilter<SaasTenantQueryDto> filter) {
        Page<SaasTenant> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 列表查询租户
     */
    @SaCheckPermission("api:system:tenant:list")
    @PostMapping("list")
    public Result<List<SaasTenant>> list(@RequestBody @Valid SaasTenantQueryDto filter) {
        return success(this.service.list(filter));
    }

    /**
     * 查询租户详情
     *
     * @param id 租户主键ID
     */
    @SaCheckPermission("api:system:tenant:list")
    @GetMapping("get/{id}")
    public Result<SaasTenant> get(@PathVariable Long id,
                                  @RequestParam(required = false) String serviceCode) {
        SaasTenant tenant = this.service.getDetailById(id, serviceCode);
        return success(tenant);
    }

    /**
     * 查询租户数据源列表
     */
    @SaCheckPermission("api:system:tenant:list")
    @GetMapping("datasource/list/{tenantId}")
    public Result<List<SaasTenantDatasource>> listDatasource(@PathVariable Long tenantId) {
        return success(service.listDatasource(tenantId));
    }

    /**
     * 保存或更新租户数据源
     */
    @RepeatSubmit
    @SaCheckPermission("api:system:tenant:edit")
    @PostMapping("datasource/saveOrUpdate")
    public Result<Long> saveOrUpdateDatasource(@RequestBody @Valid SaasTenantDatasourceSaveDto dto) {
        return success(service.saveOrUpdateDatasource(dto));
    }

    /**
     * 删除租户数据源
     */
    @RepeatSubmit
    @SaCheckPermission("api:system:tenant:edit")
    @DeleteMapping("datasource/delete/{id}")
    public Result<Boolean> deleteDatasource(@PathVariable Long id) {
        return success(service.removeDatasource(id));
    }

}
