package com.yz.mall.seata.at.order.feign;

import com.yz.mall.web.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author yunze
 * @date 2023/11/6 0006 23:46
 */
@Repository
@FeignClient(name = "${yz.feign.server-name.mall-account:mall-account}", path = "/account")
public interface TAccountFeignService {

    /**
     * 扣减余额
     *
     * @param accountId 账号ID
     * @param amount    扣减金额
     */
    @RequestMapping("/deduct")
    public Result<Boolean> deduct(@RequestParam("accountId") Long accountId, @RequestParam("amountToDeduct") BigDecimal amountToDeduct);
}
