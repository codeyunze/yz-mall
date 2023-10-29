package com.yz.openinterface.business.shop.stock.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.common.extension.api.ApiController;
import com.yz.common.vo.Result;
import com.yz.openinterface.business.shop.stock.entity.ShopStock;
import com.yz.openinterface.business.shop.stock.service.ShopStockService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 库存表(ShopStock)表控制层
 *
 * @author yunze
 * @since 2023-02-06 22:44:54
 */
@RestController
@RequestMapping("shopStock")
public class ShopStockController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ShopStockService shopStockService;

    /**
     * 分页查询所有数据
     *
     * @param page      分页对象
     * @param shopStock 查询实体
     * @return 所有数据
     */
    @GetMapping
    public Result<Page<ShopStock>> selectAll(Page<ShopStock> page, ShopStock shopStock) {
        return success(this.shopStockService.page(page, new QueryWrapper<>(shopStock)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Result<ShopStock> selectOne(@PathVariable Serializable id) {
        return success(this.shopStockService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param shopStock 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Result<Boolean> insert(@RequestBody ShopStock shopStock) {
        return success(this.shopStockService.save(shopStock));
    }

    /**
     * 修改数据
     *
     * @param shopStock 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody ShopStock shopStock) {
        return success(this.shopStockService.updateById(shopStock));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return success(this.shopStockService.removeByIds(idList));
    }
}

