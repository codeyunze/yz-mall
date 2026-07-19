package com.yz.mall.oms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.oms.dto.OmsOrderQuerySlimDto;
import com.yz.mall.oms.service.OmsOrderService;
import com.yz.mall.oms.vo.OmsOrderDetailVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单通用接口
 *
 * @author yunze
 * @since 2025-01-30 19:12:59
 */
@RestController
@RequestMapping("oms/order")
public class OmsOrderController extends ApiController {

    private final OmsOrderService service;

    public OmsOrderController(OmsOrderService service) {
        this.service = service;
    }

    /**
     * 订单详细信息查询
     *
     * @param query 订单编号或订单Id
     * @return 订单详细信息
     */
    @SaCheckPermission("api:oms:order:add")
    @PostMapping("get")
    public Result<OmsOrderDetailVo> get(@RequestBody OmsOrderQuerySlimDto query) {
        return success(this.service.get(StpUtil.getLoginIdAsLong(), query));
    }
}
