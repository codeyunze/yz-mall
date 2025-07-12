package com.yz.mall.oms.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.oms.dto.InternalOmsOrderByCartDto;
import com.yz.mall.oms.dto.InternalOmsOrderDto;
import com.yz.mall.oms.dto.OmsOrderQueryDto;
import com.yz.mall.oms.dto.OmsOrderQuerySlimDto;
import com.yz.mall.oms.entity.OmsOrder;
import com.yz.mall.oms.service.OmsOrderService;
import com.yz.mall.oms.vo.OmsOrderDetailVo;
import com.yz.mall.oms.vo.OmsOrderSlimVo;
import com.yz.mall.oms.vo.OmsOrderVo;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import com.yz.mall.web.enums.CodeEnum;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 我的订单信息
 *
 * @author yunze
 * @since 2025-01-30 19:12:59
 */
@RestController
@RequestMapping("oms/order/mine")
public class OmsOrderMineController extends ApiController {

    /**
     * 服务对象
     */
    private final OmsOrderService service;

    public OmsOrderMineController(OmsOrderService service) {
        this.service = service;
    }

    /**
     * 根据购物车选择信息生成订单
     */
    @SaCheckPermission("api:oms:order:add")
    @PostMapping("generate")
    public Result<OmsOrderSlimVo> generateOrder(@RequestBody @Valid InternalOmsOrderByCartDto dto) {
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

    /**
     * 用户分页查询自己的订单信息
     */
    @SaCheckPermission("api:oms:order:add")
    @PostMapping("/page")
    public Result<ResultTable<OmsOrderVo>> minePage(@RequestBody PageFilter<OmsOrderQueryDto> filter) {
        filter.getFilter().setUserId(StpUtil.getLoginIdAsLong());
        Page<OmsOrderVo> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 订单详细信息查询
     * @return 订单详细信息
     */
    @SaCheckPermission("api:oms:order:add")
    @PostMapping("get")
    public Result<OmsOrderDetailVo> get(@RequestBody OmsOrderQuerySlimDto query) {
        return success(this.service.get(StpUtil.getLoginIdAsLong(), query));
    }

    /**
     * 取消订单
     *
     * @param id 订单Id {@link OmsOrder#getId()}
     */
    @SaCheckPermission("api:oms:order:add")
    @PostMapping("cancel/{id}")
    public Result<Boolean> cancel(@PathVariable Long id) {
        boolean cancelled = this.service.cancelById(id);
        return cancelled ? success(true) : new Result<>(CodeEnum.BUSINESS_ERROR.get(), false, "订单取消失败");
    }
}

