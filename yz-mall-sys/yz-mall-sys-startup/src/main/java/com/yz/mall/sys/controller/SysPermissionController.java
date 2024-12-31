package com.yz.mall.sys.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysPermissionAddDto;
import com.yz.mall.sys.dto.SysPermissionQueryDto;
import com.yz.mall.sys.dto.SysPermissionUpdateDto;
import com.yz.mall.sys.entity.SysPermission;
import com.yz.mall.sys.service.SysPermissionService;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 系统-权限数据表(SysPermission)表控制层
 *
 * @author yunze
 * @since 2024-11-17 20:08:25
 */
@RestController
@RequestMapping("sys/permission")
public class SysPermissionController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private SysPermissionService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysPermissionAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysPermissionUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<ResultTable<SysPermission>> page(@RequestBody @Valid PageFilter<SysPermissionQueryDto> filter) {
        Page<SysPermission> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysPermission> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

