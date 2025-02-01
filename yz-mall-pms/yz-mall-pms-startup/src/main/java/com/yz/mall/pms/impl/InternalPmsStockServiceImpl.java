package com.yz.mall.pms.impl;

import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.service.InternalPmsStockService;
import com.yz.mall.pms.service.PmsStockService;
import com.yz.mall.pms.vo.InternalPmsStockDeductVo;
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
    public Boolean deduct(InternalPmsStockDto deductStock) {
        return service.deduct(deductStock);
    }

    @Override
    public List<InternalPmsStockDeductVo> deductBatch(List<InternalPmsStockDto> productStocks) {
        return service.deduct(productStocks);
    }

    @Override
    public Boolean add(InternalPmsStockDto dto) {
        return service.add(dto);
    }

    @Override
    public Integer getStockByProductId(Long productId) {
        return service.getStockByProductId(productId);
    }
}
