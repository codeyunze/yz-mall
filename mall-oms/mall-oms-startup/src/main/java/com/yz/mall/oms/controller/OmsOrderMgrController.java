package com.yz.mall.oms.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.oms.dto.OmsOrderQueryDto;
import com.yz.mall.oms.dto.OmsOrderQuerySlimDto;
import com.yz.mall.oms.entity.OmsOrder;
import com.yz.mall.oms.service.OmsOrderService;
import com.yz.mall.oms.vo.OmsOrderDetailVo;
import com.yz.mall.oms.vo.OmsOrderVo;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 订单管理信息
 *
 * @author yunze
 * @since 2025-01-30 19:12:59
 */
@RestController
@RequestMapping("oms/order/mgr")
public class OmsOrderMgrController extends ApiController {

    /**
     * 服务对象
     */
    private final OmsOrderService service;

    public OmsOrderMgrController(OmsOrderService service) {
        this.service = service;
    }

    /**
     * 单位管理员查询管辖的订单信息
     */
    @SaCheckPermission("api:oms:order:add")
    @PostMapping("/page")
    public Result<ResultTable<OmsOrderVo>> mgrPage(@RequestBody PageFilter<OmsOrderQueryDto> filter) {
        // filter.getFilter().setUserId(StpUtil.getLoginIdAsLong());
        filter.getFilter().setExcludeOrderStatuses(Arrays.asList(0, 5, 6));
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

