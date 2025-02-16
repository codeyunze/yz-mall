package com.yz.mall.file.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.file.dto.SysFilesAddDto;
import com.yz.mall.file.dto.SysFilesQueryDto;
import com.yz.mall.file.dto.SysFilesUpdateDto;
import com.yz.mall.file.entity.SysFiles;
import com.yz.mall.file.service.SysFilesService;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 系统-文件表(SysFiles)表控制层
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
@RestController
@RequestMapping("/file")
public class SysFilesController extends ApiController {

    /**
     * 服务对象
     */
    private final SysFilesService service;

    public SysFilesController(SysFilesService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysFilesAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysFilesUpdateDto dto) {
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
    public Result<ResultTable<SysFiles>> page(@RequestBody @Valid PageFilter<SysFilesQueryDto> filter) {
        Page<SysFiles> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysFiles> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

