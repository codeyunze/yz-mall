package com.yz.dynamic.datasource.two.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.dynamic.datasource.two.entity.TStorage;
import com.yz.dynamic.datasource.two.service.TStorageService;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * 库存信息(TStock)表控制层
 *
 * @author yunze
 * @since 2023-10-31 00:02:30
 */
@RestController
@RequestMapping("tStorage")
public class TStorageController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private TStorageService tStockService;

    /**
     * 分页查询所有数据
     *
     * @param filter 分页查询过滤条件
     * @return 所有数据
     */
    @PostMapping("/page")
    public Result<ResultTable<TStorage>> page(@RequestBody @Validated PageFilter<TStorage> filter) {
        Page<TStorage> page = this.tStockService.page(new Page<>(filter.getCurrent(), filter.getSize()), new QueryWrapper<>(filter.getFilter()));
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/get/{id}")
    public Result<TStorage> selectOne(@PathVariable Serializable id) {
        return success(this.tStockService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param tStock 实体对象
     * @return 新增结果
     */
    @PostMapping("/add")
    public Result<Boolean> insert(@RequestBody @Validated TStorage tStock) {
        return success(this.tStockService.save(tStock));
    }

    /**
     * 修改数据
     *
     * @param tStock 实体对象
     * @return 修改结果
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody @Validated TStorage tStock) {
        return success(this.tStockService.updateById(tStock));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.tStockService.removeById(id));
    }
}

