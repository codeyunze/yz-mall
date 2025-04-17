package com.yz.mall.pms.service.impl;

import com.yz.mall.pms.dto.InternalPmsProductSlimVo;
import com.yz.mall.pms.feign.InternalPmsProductFeign;
import com.yz.mall.pms.service.InternalPmsProductService;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.enums.CodeEnum;
import com.yz.mall.web.exception.BusinessException;
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

    private final InternalPmsProductFeign service;

    public InternalPmsProductServiceImpl(InternalPmsProductFeign service) {
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
