package com.yz.mall.pms.feign;

import com.yz.mall.pms.dto.PmsStockDto;
import com.yz.tools.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author yunze
 * @date 2024/6/23 16:26
 */
@FeignClient(name = "yz-mall-pms", contextId = "internalStock")
@RequestMapping("internal/pms/stock")
public interface InternalPmsStockFeign {

    /**
     * 扣减商品库存
     */
    @PostMapping("deduct")
    public Result<Boolean> deduct(@RequestBody @Valid PmsStockDto dto);

    /**
     * 增加商品库存
     */
    @PostMapping("add")
    public Result<Boolean> add(@RequestBody @Valid PmsStockDto dto);

    /**
     * 获取指定商品的库存
     */
    @GetMapping("getStock/{productId}")
    public Result<Integer> getStockByProductId(@PathVariable String productId);
}
