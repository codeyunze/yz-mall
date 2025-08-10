package com.yz.mall.pms.service.impl;

import com.yz.mall.pms.dto.ExtendPmsStockDto;
import com.yz.mall.pms.service.ExtendPmsStockService;
import com.yz.mall.pms.service.PmsStockService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yunze
 * @date 2025/8/9 星期六 18:56
 */
@Service
public class ExtendPmsStockServiceImpl implements ExtendPmsStockService {

    private final PmsStockService pmsStockService;

    public ExtendPmsStockServiceImpl(PmsStockService pmsStockService) {
        this.pmsStockService = pmsStockService;
    }

    @Override
    public Boolean deduct(ExtendPmsStockDto deductStock) {
        return pmsStockService.deduct(deductStock);
    }

    @Override
    public Boolean deductBatch(List<ExtendPmsStockDto> productStocks) {
        pmsStockService.deduct(productStocks);
        return Boolean.TRUE;
    }

    @Override
    public Boolean add(ExtendPmsStockDto dto) {
        return pmsStockService.add(dto);
    }

    @Override
    public Integer getStockByProductId(Long productId) {
        return pmsStockService.getStockByProductId(productId);
    }
}
