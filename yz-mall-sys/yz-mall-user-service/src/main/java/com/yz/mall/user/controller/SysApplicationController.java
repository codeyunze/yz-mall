package com.yz.mall.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.user.dto.SysApplicationAddDto;
import com.yz.mall.user.dto.SysApplicationQueryDto;
import com.yz.mall.user.dto.SysApplicationUpdateDto;
import com.yz.mall.user.entity.SysApplication;
import com.yz.mall.user.service.SysApplicationService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 应用配置(SysApplication)表控制层
 *
 * @author yunze
 * @since 2024-08-11 20:10:14
 * @deprecated 需要从service模块迁移到core模块
 */
@Deprecated
@RestController
@RequestMapping("sysApplication")
public class SysApplicationController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private SysApplicationService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<String> insert(@RequestBody @Valid SysApplicationAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysApplicationUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable String id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<List<SysApplication>> page(@RequestBody @Valid PageFilter<SysApplicationQueryDto> filter) {
        Page<SysApplication> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysApplication> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

