package com.yz.mall.ss.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.ss.dto.SeataStockAddDto;
import com.yz.mall.ss.dto.SeataStockQueryDto;
import com.yz.mall.ss.dto.SeataStockUpdateDto;
import com.yz.mall.ss.entity.SeataStock;
import com.yz.mall.ss.service.SeataStockService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


/**
 * 分布式事务-库存表(SeataStock)表控制层
 *
 * @author yunze
 * @since 2025-11-26 15:29:47
 */
@RestController
@RequestMapping("seataStock")
public class SeataStockController extends ApiController {

    /**
     * 服务对象
     */
    private final SeataStockService service;

    public SeataStockController(SeataStockService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SeataStockAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SeataStockUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键 ID
     */
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<ResultTable<SeataStock>> page(@RequestBody @Valid PageFilter<SeataStockQueryDto> filter) {
        Page<SeataStock> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SeataStock> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

    /**
     * 扣减库存
     *
     * @param productId    商品 Id
     * @param productStock 扣减库存数量
     */
    @PostMapping("decreaseStock")
    public Result<Boolean> decreaseStock(@RequestParam Long productId, @RequestParam Integer productStock) {
        return success(this.service.decreaseStock(productId, productStock));
    }
}

