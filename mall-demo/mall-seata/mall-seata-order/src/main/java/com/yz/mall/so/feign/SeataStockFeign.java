package com.yz.mall.so.feign;

import com.yz.mall.base.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yunze
 * @since 2025/11/26 22:18
 */
@FeignClient(name = "mall-seata-stock", contextId = "extendStock", path = "seataStock")
public interface SeataStockFeign {

    /**
     * 扣减库存
     *
     * @param productId    商品 Id
     * @param productStock 扣减库存数量
     */
    @PostMapping("decreaseStock")
    Result<Boolean> decreaseStock(@RequestParam Long productId, @RequestParam Integer productStock);
}
