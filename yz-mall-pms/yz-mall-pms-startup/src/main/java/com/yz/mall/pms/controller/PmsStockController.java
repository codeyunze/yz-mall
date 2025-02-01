package com.yz.mall.pms.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.dto.PmsStockQueryDto;
import com.yz.mall.pms.entity.PmsStock;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.service.PmsStockService;
import com.yz.mall.pms.vo.InternalPmsStockDeductVo;
import com.yz.mall.pms.vo.PmsProductStockVo;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 商品库存表(PmsStock)表控制层
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
@RestController
@RequestMapping("pms/stock")
public class PmsStockController extends ApiController {

    private final PmsStockService service;

    public PmsStockController(PmsStockService service) {
        this.service = service;
    }


    /**
     * 分页查询
     */
    @SaCheckPermission("api:pms:stock:page")
    @PostMapping("page")
    public Result<ResultTable<PmsProductStockVo>> page(@RequestBody @Valid PageFilter<PmsStockQueryDto> filter) {
        Page<PmsProductStockVo> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }


    /**
     * 详情查询
     */
    @SaCheckPermission("api:pms:stock:page")
    @GetMapping("get/{id}")
    public Result<PmsStock> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }


    /**
     * 扣减商品库存
     */
    @SaCheckPermission("api:pms:stock:deduct")
    @PostMapping("deduct")
    public Result<Boolean> deduct(@RequestBody @Valid InternalPmsStockDto dto) {
        return success(this.service.deduct(dto));
    }


    /**
     * 扣减商品库存
     */
    @SaCheckPermission("api:pms:stock:deduct")
    @PostMapping("deducts")
    public Result<List<InternalPmsStockDeductVo>> deducts(@RequestBody @Valid List<InternalPmsStockDto> productStocks) {
        return success(this.service.deduct(productStocks));
    }


    /**
     * 增加商品库存
     */
    @SaCheckPermission("api:pms:stock:add")
    @PostMapping("add")
    public Result<Boolean> add(@RequestBody @Valid InternalPmsStockDto dto) {
        return success(this.service.add(dto));
    }
}

