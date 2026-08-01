package com.yz.mall.pms.service;

import com.yz.mall.pms.dto.ExtendPmsSkuSlimVo;

import java.util.List;

/**
 * 内部开放接口: 商品SKU
 */
public interface ExtendPmsSkuService {

    /**
     * 根据 SKU Id 批量查询
     *
     * @param skuIds SKU Id 列表
     * @return SKU 信息
     */
    List<ExtendPmsSkuSlimVo> getSkuByIds(List<Long> skuIds);
}
