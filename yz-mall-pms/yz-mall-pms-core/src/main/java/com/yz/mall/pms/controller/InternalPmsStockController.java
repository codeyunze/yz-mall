package com.yz.mall.pms.controller;

import com.yz.mall.pms.dto.PmsStockDto;
import com.yz.mall.pms.service.InternalPmsStockService;
import com.yz.tools.ApiController;
import com.yz.tools.Result;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author yunze
 * @date 2024/6/23 16:21
 */
@RestController
@RequestMapping("internal/pms/stock")
public class InternalPmsStockController extends ApiController {

    private final InternalPmsStockService service;

    public InternalPmsStockController(InternalPmsStockService service) {
        this.service = service;
    }

    /**
     * 扣减商品库存
     */
    @PostMapping("deduct")
    public Result<Boolean> deduct(@RequestBody @Valid PmsStockDto dto) {
        return success(this.service.deduct(dto.getProductId(), dto.getQuantity()));
    }

    /**
     * 增加商品库存
     */
    @PostMapping("add")
    public Result<Boolean> add(@RequestBody @Valid PmsStockDto dto) {
        return success(this.service.add(dto.getProductId(), dto.getQuantity()));
    }

    /**
     * 获取指定商品的库存
     */
    @GetMapping("getStock/{productId}")
    public Result<Integer> getStockByProductId(@PathVariable String productId) {
        return success(this.service.getStockByProductId(productId));
    }

}
