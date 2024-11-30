package com.yz.mall.sys.internal;


import com.yz.mall.sys.dto.InternalLoginInfoDto;
import com.yz.mall.sys.dto.InternalSysUserBalanceDto;
import com.yz.mall.sys.dto.InternalSysUserCheckLoginDto;
import com.yz.mall.sys.service.InternalSysUserService;
import com.yz.tools.ApiController;
import com.yz.tools.Result;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 基础-用户(BaseUser)表控制层
 *
 * @author yunze
 * @since 2024-06-16 23:25:55
 */
@RestController
@RequestMapping("internal/sys/user")
public class InternalSysUserController extends ApiController {

    private final InternalSysUserService service;

    public InternalSysUserController(InternalSysUserService service) {
        this.service = service;
    }

    /**
     * 扣减余额
     */
    @PostMapping("deduct")
    public Result<Boolean> deduct(@RequestBody @Valid InternalSysUserBalanceDto dto) {
        service.deduct(dto.getUserId(), dto.getAmount());
        return success(true);
    }

    /**
     * 账户充值
     */
    @PostMapping("recharge")
    public Result<Boolean> recharge(@RequestBody @Valid InternalSysUserBalanceDto dto) {
        service.recharge(dto.getUserId(), dto.getAmount());
        return success(true);
    }

    /**
     * 登录校验
     */
    @PostMapping("checkLogin")
    public Result<InternalLoginInfoDto> checkLogin(@RequestBody @Valid InternalSysUserCheckLoginDto dto) {
        InternalLoginInfoDto loginInfo = service.checkLogin(dto);
        return success(loginInfo);
    }

    /**
     * 获取指定用户所拥有的角色
     *
     * @param userId 用户Id
     * @return 用户所拥有的角色
     */
    @GetMapping("getUserRoles/{userId}")
    public Result<List<Long>> getUserRoles(@PathVariable Long userId) {
        return success(service.getUserRoles(userId));
    }
}

