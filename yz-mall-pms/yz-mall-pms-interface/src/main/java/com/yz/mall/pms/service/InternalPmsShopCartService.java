package com.yz.mall.pms.service;

import com.yz.mall.pms.dto.InternalPmsCartDto;
import com.yz.mall.pms.dto.InternalPmsStockDto;

import java.util.List;
import java.util.Map;

/**
 * 内部开放接口: 购物车信息
 *
 * @author yunze
 * @date 2024/6/23 09:14
 */
public interface InternalPmsShopCartService {

    /**
     * 根据指定的购物车信息Id获取用户购物车里指定商品相关信息
     *
     * @param userId  指定的用户Id
     * @param cartIds 指定的商品Id列表
     * @return Map<购物车信息id, 商品信息>
     */
    Map<Long, InternalPmsCartDto> getCartByIds(Long userId, List<Long> cartIds);

    /**
     * 根据指定的购物车信息Id获取用户购物车里指定商品相关信息
     *
     * @param userId  指定的用户Id
     * @param cartIds 指定的商品Id列表
     * @return 是否操作成功 true: 成功，false：失败
     */
    boolean removeCartByIds(Long userId, List<Long> cartIds);

    /**
     * 根据指定商品信息，扣除用户购物车信息
     *
     * @param userId   指定的用户Id
     * @param products 指定的商品Id和数量
     * @return 是否操作成功 true: 成功，false：失败
     */
    boolean removeCartByProductIds(Long userId, List<InternalPmsStockDto> products);
}
