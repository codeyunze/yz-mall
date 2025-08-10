package com.yz.mall.pms.extend;

import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.base.Result;
import com.yz.mall.pms.dto.ExtendPmsCartDto;
import com.yz.mall.pms.dto.ExtendPmsStockDto;
import com.yz.mall.pms.service.ExtendPmsShopCartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 内部暴露接口: 购物车
 * @author yunze
 * @date 2025/8/10 星期日 9:24
 */
@RestController
@RequestMapping("extend/pms/cart")
public class ExtendPmsShopCartController {

    private final ExtendPmsShopCartService extendPmsShopCartService;

    public ExtendPmsShopCartController(ExtendPmsShopCartService extendPmsShopCartService) {
        this.extendPmsShopCartService = extendPmsShopCartService;
    }

    /**
     * 根据指定的购物车信息Id获取用户购物车里指定商品相关信息
     *
     * @param cartIds 指定的商品Id列表
     * @return Map<购物车信息id, 商品信息>
     */
    @PostMapping("getCartByIds")
    public Result<Map<Long, ExtendPmsCartDto>> getCartByIds(@RequestBody List<Long> cartIds) {
        Map<Long, ExtendPmsCartDto> cartByIds = extendPmsShopCartService.getCartByIds(StpUtil.getLoginIdAsLong(), cartIds);
        return Result.success(cartByIds);
    }

    /**
     * 根据指定的购物车信息Id获取用户购物车里指定商品相关信息
     *
     * @param cartIds 指定的商品Id列表
     * @return 是否操作成功 true: 成功，false：失败
     */
    @DeleteMapping("removeCartByIds")
    public Result<Boolean> removeCartByIds(@RequestBody List<Long> cartIds) {
        return Result.success(extendPmsShopCartService.removeCartByIds(StpUtil.getLoginIdAsLong(), cartIds));
    }

    /**
     * 根据指定商品信息，扣除用户购物车信息
     *
     * @param products 指定的商品Id和数量
     * @return 是否操作成功 true: 成功，false：失败
     */
    @DeleteMapping("removeCartByProductIds")
    public Result<Boolean> removeCartByProductIds(@RequestBody List<ExtendPmsStockDto> products) {
        return Result.success(extendPmsShopCartService.removeCartByProductIds(StpUtil.getLoginIdAsLong(), products));
    }

}
