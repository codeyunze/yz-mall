package com.yz.mall.pms.service.impl;

import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.pms.dto.InternalPmsProductSlimVo;
import com.yz.mall.pms.feign.ExtendPmsProductFeign;
import com.yz.mall.pms.service.InternalPmsProductService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 内部开放接口实现类: 商品信息
 *
 * @author yunze
 * @since 2025/3/16 18:59
 */
@Service
public class InternalPmsProductServiceImpl implements InternalPmsProductService {

    private final ExtendPmsProductFeign service;

    public InternalPmsProductServiceImpl(ExtendPmsProductFeign service) {
        this.service = service;
    }

    @Override
    public List<InternalPmsProductSlimVo> getProductByProductIds(List<Long> productIds) {
        Result<List<InternalPmsProductSlimVo>> result = service.getProductByProductIds(productIds);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }
}
