package com.yz.mall.pms.service;

import com.yz.mall.pms.dto.InternalPmsProductSlimVo;

import java.util.List;

/**
 * 内部开放接口: 商品信息
 *
 * @author yunze
 * @date 2025/03/16 18:54
 */
public interface InternalPmsProductService {

    /**
     * 根据商品Id查询对应的商品信息
     *
     * @param productIds 商品Id
     * @return 商品信息
     */
    List<InternalPmsProductSlimVo> getProductByProductIds(List<Long> productIds);
}
