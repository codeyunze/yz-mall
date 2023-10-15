package com.yz.redistools.common;

/**
 * @author yunze
 * @date 2023/10/15 0015 23:20
 */
public class RedisCacheKey {

    /**
     * 商品信息缓存
     * @param productId 商品ID
     * @return 商品信息缓存Key
     */
    public static String productCacheKey(Long productId) {
        return "shop:product:" + productId + ":info";
    }

    /**
     * 商品库存信息缓存
     * @param productId 商品ID
     * @return 商品库存信息Key
     */
    public static String productStockCacheKey(Long productId) {
        return "shop:product:" + productId + ":stock";
    }
}
