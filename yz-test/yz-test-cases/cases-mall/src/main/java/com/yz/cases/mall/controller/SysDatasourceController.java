package com.yz.cases.mall.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.cases.mall.dto.SysDatasourceAddDto;
import com.yz.cases.mall.dto.SysDatasourceQueryDto;
import com.yz.cases.mall.dto.SysDatasourceUpdateDto;
import com.yz.cases.mall.entity.SysDatasource;
import com.yz.cases.mall.service.SysDatasourceService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 系统-数据源信息(SysDatasource)表控制层
 *
 * @author yunze
 * @since 2024-06-12 11:02:09
 */
@RestController
@RequestMapping("sys/datasource")
public class SysDatasourceController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private SysDatasourceService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysDatasourceAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysDatasourceUpdateDto dto) {
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
    public Result<ResultTable<SysDatasource>> page(@RequestBody @Valid PageFilter<SysDatasourceQueryDto> filter) {
        Page<SysDatasource> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysDatasource> page(@PathVariable Long id) {
        return success(this.service.getById(id));
    }

}

