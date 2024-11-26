package com.yz.mall.sys.feign;

import com.yz.mall.sys.dto.InternalLoginInfoDto;
import com.yz.mall.sys.dto.InternalSysUserBalanceDto;
import com.yz.mall.sys.dto.InternalSysUserCheckLoginDto;
import com.yz.tools.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author yunze
 * @date 2024/6/22 23:44
 */
@FeignClient(name = "yz-mall-sys", contextId = "internalSysUser", path = "internal/sys/user")
public interface InternalSysUserFeign {

    /**
     * 扣减余额
     */
    @PostMapping("deduct")
    public Result<Boolean> deduct(@RequestBody @Valid InternalSysUserBalanceDto dto);

    /**
     * 账户充值
     */
    @PostMapping("recharge")
    public Result<Boolean> recharge(@RequestBody @Valid InternalSysUserBalanceDto dto);

    /**
     * 登录校验
     */
    @PostMapping("checkLogin")
    public Result<InternalLoginInfoDto> checkLogin(@RequestBody @Valid InternalSysUserCheckLoginDto dto);
}
