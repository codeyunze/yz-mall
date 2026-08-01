package com.yz.mall.pms.service.impl;

import com.yz.mall.pms.dto.ExtendPmsStockDto;
import com.yz.mall.pms.feign.ExtendPmsStockFeign;
import com.yz.mall.pms.service.ExtendPmsStockService;
import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yunze
 * @date 2024/6/23 16:25
 */
@Service
public class ExtendPmsStockServiceImpl implements ExtendPmsStockService {

    private final ExtendPmsStockFeign feign;

    @Autowired
    public ExtendPmsStockServiceImpl(ExtendPmsStockFeign feign) {
        this.feign = feign;
    }

    @Override
    public Boolean deduct(ExtendPmsStockDto deductStock) {
        Result<Boolean> result = feign.deduct(deductStock);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }

    @Override
    public Boolean deductBatch(List<ExtendPmsStockDto> productStocks) {
        Result<Boolean> result = feign.deductBatch(productStocks);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }

    @Override
    public Boolean add(ExtendPmsStockDto dto) {
        Result<Boolean> result = feign.add(dto);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }

    @Override
    public Integer getStockBySkuId(Long skuId) {
        Result<Integer> result = feign.getStockBySkuId(skuId);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }
}
