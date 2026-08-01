package com.yz.mall.pms.service.impl;

import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.pms.dto.ExtendPmsSkuSlimVo;
import com.yz.mall.pms.feign.ExtendPmsSkuFeign;
import com.yz.mall.pms.service.ExtendPmsSkuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 内部开放接口 Feign 实现: 商品SKU
 */
@Service
public class ExtendPmsSkuServiceImpl implements ExtendPmsSkuService {

    private final ExtendPmsSkuFeign service;

    public ExtendPmsSkuServiceImpl(ExtendPmsSkuFeign service) {
        this.service = service;
    }

    @Override
    public List<ExtendPmsSkuSlimVo> getSkuByIds(List<Long> skuIds) {
        Result<List<ExtendPmsSkuSlimVo>> result = service.getSkuByIds(skuIds);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData() == null ? List.of() : result.getData();
    }
}
