package com.yz.seata.mall.storage.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.seata.mall.storage.entity.TStorage;
import com.yz.seata.mall.storage.service.TStorageService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.tools.TableResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 库存信息(TStock)表控制层
 *
 * @author yunze
 * @since 2023-11-05 15:59:36
 */
@RestController
@RequestMapping("storage")
public class TStorageController extends ApiController {

    /**
     * 服务对象
     */
    private TStorageService tStockService;

    @Autowired
    void tStockController(TStorageService service) {
        this.tStockService = service;
    }

    /**
     * 分页查询所有数据
     *
     * @param filter 分页查询过滤条件
     * @return 所有数据
     */
    @PostMapping("/page")
    public TableResult<List<TStorage>> page(@RequestBody @Validated PageFilter<TStorage> filter) {
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

    /**
     * 扣减指定商品的库存
     *
     * @param productId 商品ID
     * @param num       扣减数量
     */
    @RequestMapping("/deduct")
    public Result<Boolean> deduct(@RequestParam("productId") Long productId, @RequestParam("num") Integer num) {
        boolean deduct = this.tStockService.deduct(productId, num);
        return deduct ? success(true) : failed("库存扣减失败");
    }
}

