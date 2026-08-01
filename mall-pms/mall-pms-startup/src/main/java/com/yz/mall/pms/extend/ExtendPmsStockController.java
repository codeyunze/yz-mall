package com.yz.mall.pms.extend;

import com.yz.mall.base.Result;
import com.yz.mall.pms.dto.ExtendPmsStockDto;
import com.yz.mall.pms.service.ExtendPmsStockService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部暴露接口: 商品库存信息
 *
 * @author yunze
 * @date 2025/8/9 星期六 18:57
 */
@RestController
@RequestMapping("extend/pms/stock")
public class ExtendPmsStockController {

    private final ExtendPmsStockService extendPmsStockService;

    public ExtendPmsStockController(ExtendPmsStockService extendPmsStockService) {
        this.extendPmsStockService = extendPmsStockService;
    }

    @PostMapping("deduct")
    public Result<Boolean> deduct(@RequestBody @Valid ExtendPmsStockDto dto) {
        return Result.success(extendPmsStockService.deduct(dto));
    }

    /**
     * 扣减商品库存
     */
    @PostMapping("deductBatch")
    public Result<Boolean> deductBatch(@RequestBody @Valid List<ExtendPmsStockDto> dto) {
        return Result.success(extendPmsStockService.deductBatch(dto));
    }

    /**
     * 增加商品库存
     */
    @PostMapping("add")
    public Result<Boolean> add(@RequestBody @Valid ExtendPmsStockDto dto) {
        return Result.success(extendPmsStockService.add(dto));
    }

    /**
     * 获取指定SKU的库存
     */
    @GetMapping("get/{skuId}")
    public Result<Integer> getStockBySkuId(@PathVariable Long skuId) {
        return Result.success(extendPmsStockService.getStockBySkuId(skuId));
    }
}
