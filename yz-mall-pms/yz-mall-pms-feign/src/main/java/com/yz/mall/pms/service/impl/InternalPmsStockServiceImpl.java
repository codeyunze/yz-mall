package com.yz.mall.pms.service.impl;

import com.yz.advice.exception.BusinessException;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.feign.InternalPmsStockFeign;
import com.yz.mall.pms.service.InternalPmsStockService;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yunze
 * @date 2024/6/23 16:25
 */
@Service
public class InternalPmsStockServiceImpl implements InternalPmsStockService {

    private final InternalPmsStockFeign feign;

    @Autowired
    public InternalPmsStockServiceImpl(InternalPmsStockFeign feign) {
        this.feign = feign;
    }

    @Override
    public Boolean deduct(Long productId, Integer quantity) {
        Result<Boolean> result = feign.deduct(new InternalPmsStockDto(productId, quantity));
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }

    @Override
    public void deductBatch(List<InternalPmsStockDto> productStocks) {
        Result<Boolean> result = feign.deductBatch(productStocks);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
    }

    @Override
    public Boolean add(Long productId, Integer quantity) {
        Result<Boolean> result = feign.add(new InternalPmsStockDto(productId, quantity));
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
