package com.yz.mall.user.feign;

import com.yz.mall.user.dto.InternalBaseUserBalanceDto;
import com.yz.tools.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author yunze
 * @date 2024/6/22 23:44
 */
@FeignClient(name = "yz-mall-user")
@RequestMapping("internal/baseUser")
public interface InternalBaseUserFeign {

    /**
     * 扣减余额
     */
    @PostMapping("deduct")
    public Result<Boolean> deduct(@RequestBody @Valid InternalBaseUserBalanceDto dto);

    /**
     * 账户充值
     */
    @PostMapping("recharge")
    public Result<Boolean> recharge(@RequestBody @Valid InternalBaseUserBalanceDto dto);
}
