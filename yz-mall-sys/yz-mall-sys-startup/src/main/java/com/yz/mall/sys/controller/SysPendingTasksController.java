package com.yz.mall.sys.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysPendingTasksAddDto;
import com.yz.mall.sys.dto.SysPendingTasksQueryDto;
import com.yz.mall.sys.dto.SysPendingTasksUpdateDto;
import com.yz.mall.sys.entity.SysPendingTasks;
import com.yz.mall.sys.service.SysPendingTasksService;
import com.yz.mall.web.common.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 系统-待办任务(SysPendingTasks)表控制层
 *
 * @author yunze
 * @since 2025-01-15 16:58:30
 */
@RestController
@RequestMapping("sys/tasks")
public class SysPendingTasksController extends ApiController {

    /**
     * 服务对象
     */
    private final SysPendingTasksService service;

    public SysPendingTasksController(SysPendingTasksService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @SaCheckPermission("api:system:task:edit")
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysPendingTasksAddDto dto) {
        dto.setCreateId(StpUtil.getLoginIdAsLong());
        return success(this.service.save(dto));
    }

    /**
     * 更新标题
     */
    @SaCheckPermission("api:system:task:edit")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysPendingTasksUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 结束任务
     */
    @SaCheckPermission("api:system:task:edit")
    @PostMapping("end")
    public Result<Boolean> end(@RequestBody @Valid IdDto dto) {
        return success(this.service.end(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @SaCheckPermission("api:system:task:edit")
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:system:task:page")
    @PostMapping("page")
    public Result<ResultTable<SysPendingTasks>> page(@RequestBody @Valid PageFilter<SysPendingTasksQueryDto> filter) {
        Page<SysPendingTasks> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @SaCheckPermission("api:system:task:page")
    @GetMapping("get/{id}")
    public Result<SysPendingTasks> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

