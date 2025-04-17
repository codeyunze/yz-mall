package com.yz.mall.pms.internal;

import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.pms.dto.InternalPmsCartDto;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.service.InternalPmsShopCartService;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.enums.CodeEnum;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 内部暴露接口: 商品库存
 *
 * @author yunze
 * @date 2024/6/23 16:21
 */
@RestController
@RequestMapping("internal/pms/cart")
public class InternalPmsShopCartController extends ApiController {

    public final InternalPmsShopCartService internalPmsShopCartService;

    public InternalPmsShopCartController(InternalPmsShopCartService internalPmsShopCartService) {
        this.internalPmsShopCartService = internalPmsShopCartService;
    }

    /**
     * 根据指定的购物车信息Id获取用户购物车里指定商品相关信息
     *
     * @param cartIds 指定的商品Id列表
     * @return Map<购物车信息id, 商品信息>
     */
    @PostMapping("getCartByIds")
    public Result<Map<Long, InternalPmsCartDto>> getCartByIds(@RequestBody List<Long> cartIds) {
        Map<Long, InternalPmsCartDto> cartMap = internalPmsShopCartService.getCartByIds(StpUtil.getLoginIdAsLong(), cartIds);
        return success(cartMap);
    }

    /**
     * 根据指定的购物车信息Id获取用户购物车里指定商品相关信息
     *
     * @param cartIds 指定的商品Id列表
     * @return 是否操作成功 true: 成功，false：失败
     */
    @DeleteMapping("removeCartByIds")
    public Result<Boolean> removeCartByIds(@RequestBody List<Long> cartIds) {
        boolean removed = internalPmsShopCartService.removeCartByIds(StpUtil.getLoginIdAsLong(), cartIds);
        return removed ? success(true) : new Result<>(CodeEnum.BUSINESS_ERROR.get(), null, "购物车商品移除失败");
    }

    /**
     * 根据指定商品信息，扣除用户购物车信息
     *
     * @param products 指定的商品Id和数量
     * @return 是否操作成功 true: 成功，false：失败
     */
    @DeleteMapping("removeCartByProductIds")
    public Result<Boolean> removeCartByProductIds(@RequestBody List<InternalPmsStockDto> products) {
        boolean removed = internalPmsShopCartService.removeCartByProductIds(StpUtil.getLoginIdAsLong(), products);
        return removed ? success(true) : new Result<>(CodeEnum.BUSINESS_ERROR.get(), null, "购物车商品移除失败");
    }

}
