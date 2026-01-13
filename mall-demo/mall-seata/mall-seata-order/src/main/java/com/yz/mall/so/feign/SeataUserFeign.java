package com.yz.mall.so.feign;

import com.yz.mall.base.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yunze
 * @since 2025/11/26 22:50
 */
@FeignClient(name = "mall-seata-user", contextId = "extendUser", path = "seataUser")
public interface SeataUserFeign {

    /**
     * 扣减用户余额
     */
    @PostMapping("decreaseBalance")
    Result<Boolean> decreaseBalance(@RequestParam Long userId, @RequestParam Long amount);
}
