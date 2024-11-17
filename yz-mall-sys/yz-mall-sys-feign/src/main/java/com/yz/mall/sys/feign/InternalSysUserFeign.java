package com.yz.mall.sys.feign;

import com.yz.mall.sys.dto.InternalSysUserBalanceDto;
import com.yz.tools.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author yunze
 * @date 2024/6/22 23:44
 */
@FeignClient(name = "yz-mall-sys", contextId = "internalSysUser")
public interface InternalSysUserFeign {

    /**
     * 扣减余额
     */
    @PostMapping("internal/sys/user/deduct")
    public Result<Boolean> deduct(@RequestBody @Valid InternalSysUserBalanceDto dto);

    /**
     * 账户充值
     */
    @PostMapping("internal/sys/user/recharge")
    public Result<Boolean> recharge(@RequestBody @Valid InternalSysUserBalanceDto dto);
}
