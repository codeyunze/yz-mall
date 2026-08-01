package com.yz.mall.pms.service;

import com.yz.mall.pms.dto.ExtendPmsStockDto;

import java.util.List;

/**
 * 内部开放接口: 商品库存信息
 * @author yunze
 * @date 2024/6/23 09:14
 */
public interface ExtendPmsStockService {

    /**
     * 扣减库存
     *
     * @param deductStock 指定的商品及其库存
     * @return 是否扣减成功
     */
    Boolean deduct(ExtendPmsStockDto deductStock);

    /**
     * 批量扣减指定商品库存
     *
     * @param productStocks 指定的商品及其库存
     */
    Boolean deductBatch(List<ExtendPmsStockDto> productStocks);

    /**
     * 增加库存
     *
     * @param dto 商品入库信息
     * @return 是否增加成功
     */
    Boolean add(ExtendPmsStockDto dto);

    /**
     * 获取指定SKU的库存
     *
     * @param skuId SKU id
     * @return SKU剩余库存
     */
    Integer getStockBySkuId(Long skuId);
}
