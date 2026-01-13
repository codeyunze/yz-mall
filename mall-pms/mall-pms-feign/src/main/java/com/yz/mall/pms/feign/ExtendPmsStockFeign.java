package com.yz.mall.pms.feign;

import com.yz.mall.base.Result;
import com.yz.mall.pms.dto.ExtendPmsStockDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author yunze
 * @date 2024/6/23 16:26
 */
@Repository
@FeignClient(name = "mall-pms", contextId = "extendPmsStock", path = "extend/pms/stock")
public interface ExtendPmsStockFeign {

    /**
     * 扣减商品库存
     */
    @PostMapping("deduct")
    Result<Boolean> deduct(@RequestBody @Valid ExtendPmsStockDto dto);

    /**
     * 扣减商品库存
     */
    @PostMapping("deductBatch")
    Result<Boolean> deductBatch(@RequestBody @Valid List<ExtendPmsStockDto> dto);

    /**
     * 增加商品库存
     */
    @PostMapping("add")
    Result<Boolean> add(@RequestBody @Valid ExtendPmsStockDto dto);

    /**
     * 获取指定SKU的库存
     */
    @GetMapping("get/{skuId}")
    Result<Integer> getStockBySkuId(@PathVariable Long skuId);
}
