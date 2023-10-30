package com.yz.dynamic.datasource.two.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.dynamic.datasource.two.entity.TProduct;
import com.yz.dynamic.datasource.two.service.TProductService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.tools.TableResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 商品信息(TProduct)表控制层
 *
 * @author yunze
 * @since 2023-10-31 00:00:28
 */
@RestController
@RequestMapping("tProduct")
public class TProductController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private TProductService tProductService;

    /**
     * 分页查询所有数据
     *
     * @param filter 分页查询过滤条件
     * @return 所有数据
     */
    @PostMapping("/page")
    public TableResult<List<TProduct>> page(@RequestBody @Validated PageFilter<TProduct> filter) {
        Page<TProduct> page = this.tProductService.page(new Page<>(filter.getCurrent(), filter.getSize()), new QueryWrapper<>(filter.getFilter()));
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/get/{id}")
    public Result<TProduct> selectOne(@PathVariable Serializable id) {
        return success(this.tProductService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param tProduct 实体对象
     * @return 新增结果
     */
    @PostMapping("/add")
    public Result<Boolean> insert(@RequestBody @Validated TProduct tProduct) {
        return success(this.tProductService.save(tProduct));
    }

    /**
     * 修改数据
     *
     * @param tProduct 实体对象
     * @return 修改结果
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody @Validated TProduct tProduct) {
        return success(this.tProductService.updateById(tProduct));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.tProductService.removeById(id));
    }
}

