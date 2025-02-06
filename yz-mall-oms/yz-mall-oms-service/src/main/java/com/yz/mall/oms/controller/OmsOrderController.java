package com.yz.mall.oms.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.oms.dto.InternalOmsOrderByCartDto;
import com.yz.mall.oms.dto.InternalOmsOrderDto;
import com.yz.mall.oms.service.OmsOrderService;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 订单信息表(OmsOrder)表控制层
 *
 * @author yunze
 * @since 2025-01-30 19:12:59
 */
@RestController
@RequestMapping("oms/order")
public class OmsOrderController extends ApiController {

    /**
     * 服务对象
     */
    private final OmsOrderService service;

    public OmsOrderController(OmsOrderService service) {
        this.service = service;
    }

    /**
     * 根据购物车选择信息生成订单
     */
    @SaCheckPermission("api:oms:order:add")
    @PostMapping("generate")
    public Result<Long> generateOrder(@RequestBody @Valid InternalOmsOrderByCartDto dto) {
        dto.setUserId(StpUtil.getLoginIdAsLong());
        return success(this.service.generateOrder(dto));
    }

    /**
     * 单个商品直接生成订单
     */
    @SaCheckPermission("api:oms:order:add")
    @PostMapping("add")
    public Result<Long> add(@RequestBody @Valid InternalOmsOrderDto dto) {
        dto.setUserId(StpUtil.getLoginIdAsLong());
        return success(this.service.add(dto));
    }
}

