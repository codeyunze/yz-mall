package com.yz.seata.mall.order.feign;

import com.yz.tools.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yunze
 * @date 2023/11/6 0006 23:46
 */
@Repository
@FeignClient(name = "${yz.feign.server-name.mall-storage:mall-storage}", path = "/storage")
public interface TStorageFeignService {

    /**
     * 扣减指定商品的库存
     *
     * @param productId 商品ID
     * @param num       扣减数量
     */
    @RequestMapping("/deduct")
    Result<Boolean> deduct(@RequestParam("productId") Long productId, @RequestParam("num") Integer num);
}
