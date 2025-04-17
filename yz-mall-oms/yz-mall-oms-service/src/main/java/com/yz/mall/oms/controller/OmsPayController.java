package com.yz.mall.oms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.IdUtil;
import com.yz.mall.oms.dto.OmsPayDto;
import com.yz.mall.oms.service.OmsOrderService;
import com.yz.mall.web.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 支付接口
 * @author yunze
 * @since 2025/4/17 19:30
 */
@RestController
@RequestMapping("oms")
public class OmsPayController {

    private final OmsOrderService orderService;

    public OmsPayController(OmsOrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 支付
     * @param dto 支付参数
     * @return 支付回执
     */
    @SaCheckLogin
    @PostMapping("pay")
    public Result<Long> pay(@RequestBody @Valid OmsPayDto dto) {
        orderService.payOrderById(dto.getBusinessId(), dto.getPayType());
        return Result.success(IdUtil.getSnowflakeNextId());
    }

}
