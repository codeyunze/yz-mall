package com.yz.mall.pms.service;

/**
 * @author yunze
 * @date 2024/6/23 09:14
 */
public interface InternalPmsStockService {

    /**
     * 扣减库存
     *
     * @param productId 商品信息
     * @param quantity  扣减数量
     * @return 是否扣减成功
     */
    Boolean deduct(String productId, Integer quantity);

    /**
     * 增加库存
     *
     * @param productId 商品信息
     * @param quantity  增加数量
     * @return 是否增加成功
     */
    Boolean add(String productId, Integer quantity);

    /**
     * 获取指定商品的库存
     *
     * @param productId 商品id
     * @return 商品剩余库存
     */
    Integer getStockByProductId(String productId);
}
