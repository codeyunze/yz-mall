package com.yz.mall.pms.service.impl;

import com.yz.mall.pms.dto.ExtendPmsSkuSlimVo;
import com.yz.mall.pms.entity.PmsSku;
import com.yz.mall.pms.service.ExtendPmsSkuService;
import com.yz.mall.pms.service.PmsSkuService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * 内部开放接口本地实现: 商品SKU
 */
@Service
public class ExtendPmsSkuServiceImpl implements ExtendPmsSkuService {

    private final PmsSkuService pmsSkuService;

    public ExtendPmsSkuServiceImpl(PmsSkuService pmsSkuService) {
        this.pmsSkuService = pmsSkuService;
    }

    @Override
    public List<ExtendPmsSkuSlimVo> getSkuByIds(List<Long> skuIds) {
        if (CollectionUtils.isEmpty(skuIds)) {
            return Collections.emptyList();
        }
        List<PmsSku> skus = pmsSkuService.listByIds(skuIds);
        if (CollectionUtils.isEmpty(skus)) {
            return Collections.emptyList();
        }
        return skus.stream().map(sku -> {
            ExtendPmsSkuSlimVo vo = new ExtendPmsSkuSlimVo();
            BeanUtils.copyProperties(sku, vo);
            return vo;
        }).toList();
    }
}
