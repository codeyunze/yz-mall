package com.yz.mall.user.controller;


import com.yz.mall.user.dto.InternalBaseUserBalanceDto;
import com.yz.mall.user.service.InternalBaseUserService;
import com.yz.tools.ApiController;
import com.yz.tools.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 基础-用户(BaseUser)表控制层
 *
 * @author yunze
 * @since 2024-06-16 23:25:55
 */
@RestController
@RequestMapping("internal/base/user")
public class InternalBaseUserController extends ApiController {

    private final InternalBaseUserService service;

    public InternalBaseUserController(InternalBaseUserService service) {
        this.service = service;
    }

    /**
     * 扣减余额
     */
    @PostMapping("deduct")
    public Result<Boolean> deduct(@RequestBody @Valid InternalBaseUserBalanceDto dto) {
        service.deduct(dto.getUserId(), dto.getAmount());
        return success(true);
    }

    /**
     * 账户充值
     */
    @PostMapping("recharge")
    public Result<Boolean> recharge(@RequestBody @Valid InternalBaseUserBalanceDto dto) {
        service.recharge(dto.getUserId(), dto.getAmount());
        return success(true);
    }

}

