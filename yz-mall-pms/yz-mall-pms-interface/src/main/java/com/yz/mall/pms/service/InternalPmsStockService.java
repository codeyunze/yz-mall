package com.yz.mall.pms.service;

import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.vo.InternalPmsStockDeductVo;

import java.util.List;

/**
 * @author yunze
 * @date 2024/6/23 09:14
 */
public interface InternalPmsStockService {

    /**
     * 扣减库存
     *
     * @param deductStock 指定的商品及其库存
     * @return 是否扣减成功
     */
    Boolean deduct(InternalPmsStockDto deductStock);

    /**
     * 批量扣减指定商品库存
     *
     * @param productStocks 指定的商品及其库存
     */
    void deductBatch(List<InternalPmsStockDto> productStocks);

    /**
     * 增加库存
     *
     * @param dto 商品入库信息
     * @return 是否增加成功
     */
    Boolean add(InternalPmsStockDto dto);

    /**
     * 获取指定商品的库存
     *
     * @param productId 商品id
     * @return 商品剩余库存
     */
    Integer getStockByProductId(Long productId);
}
