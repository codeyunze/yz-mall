package com.yz.mall.pms.internal;

import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.service.InternalPmsStockService;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public Result<Boolean> deduct(@RequestBody @Valid InternalPmsStockDto deductStock) {
        return success(this.service.deduct(deductStock));
    }

    /**
     * 扣减商品库存
     */
    @PostMapping("deductBatch")
    public Result<Boolean> deductBatch(@RequestBody @Valid List<InternalPmsStockDto> dto) {
        this.service.deductBatch(dto);
        return success(true);
    }

    /**
     * 增加商品库存
     */
    @PostMapping("add")
    public Result<Boolean> add(@RequestBody @Valid InternalPmsStockDto dto) {
        return success(this.service.add(dto));
    }

    /**
     * 获取指定商品的库存
     */
    @GetMapping("get/{productId}")
    public Result<Integer> getStockByProductId(@PathVariable Long productId) {
        return success(this.service.getStockByProductId(productId));
    }

}
