package com.yz.mall.pms.feign;

import com.yz.mall.pms.dto.InternalPmsCartDto;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.base.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 内部暴露service实现类: 购物车
 *
 * @author yunze
 * @date 2024/6/23 16:26
 */
@Repository
@FeignClient(name = "mall-pms", contextId = "extendPmsShopCart", path = "extend/pms/cart")
public interface ExtendPmsShopCartFeign {

    /**
     * 根据指定的购物车信息Id获取用户购物车里指定商品相关信息
     *
     * @param cartIds 指定的商品Id列表
     * @return Map<购物车信息id, 商品信息>
     */
    @PostMapping("getCartByIds")
    Result<Map<Long, InternalPmsCartDto>> getCartByIds(@RequestBody List<Long> cartIds);

    /**
     * 根据指定的购物车信息Id获取用户购物车里指定商品相关信息
     *
     * @param cartIds 指定的商品Id列表
     * @return 是否操作成功 true: 成功，false：失败
     */
    @DeleteMapping("removeCartByIds")
    Result<Boolean> removeCartByIds(@RequestBody List<Long> cartIds);

    /**
     * 根据指定商品信息，扣除用户购物车信息
     *
     * @param products 指定的商品Id和数量
     * @return 是否操作成功 true: 成功，false：失败
     */
    @DeleteMapping("removeCartByProductIds")
    Result<Boolean> removeCartByProductIds(@RequestBody List<InternalPmsStockDto> products);
}
