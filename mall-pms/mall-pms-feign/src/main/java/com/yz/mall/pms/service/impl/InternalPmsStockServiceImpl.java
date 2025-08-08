package com.yz.mall.pms.service.impl;

import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.feign.ExtendPmsStockFeign;
import com.yz.mall.pms.service.InternalPmsStockService;
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
public class InternalPmsStockServiceImpl implements InternalPmsStockService {

    private final ExtendPmsStockFeign feign;

    @Autowired
    public InternalPmsStockServiceImpl(ExtendPmsStockFeign feign) {
        this.feign = feign;
    }

    @Override
    public Boolean deduct(InternalPmsStockDto deductStock) {
        Result<Boolean> result = feign.deduct(deductStock);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }

    @Override
    public void deductBatch(List<InternalPmsStockDto> productStocks) {
        Result<Object> result = feign.deductBatch(productStocks);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
    }

    @Override
    public Boolean add(InternalPmsStockDto dto) {
        Result<Boolean> result = feign.add(dto);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }

    @Override
    public Integer getStockByProductId(Long productId) {
        Result<Integer> result = feign.getStockByProductId(productId);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }
}
