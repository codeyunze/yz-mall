package com.yz.mall.pms.impl;

import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.service.InternalPmsStockService;
import com.yz.mall.pms.service.PmsStockService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yunze
 * @date 2024/6/23 09:48
 */
@Service
public class InternalPmsStockServiceImpl implements InternalPmsStockService {

    private final PmsStockService service;

    public InternalPmsStockServiceImpl(PmsStockService service) {
        this.service = service;
    }

    @Override
    public Boolean deduct(Long productId, Integer quantity) {
        return service.deduct(productId, quantity);
    }

    @Override
    public void deductBatch(List<InternalPmsStockDto> productStocks) {
        service.deduct(productStocks);
    }

    @Override
    public Boolean add(Long productId, Integer quantity) {
        return service.add(productId, quantity);
    }

    @Override
    public Integer getStockByProductId(Long productId) {
        return service.getStockByProductId(productId);
    }
}
