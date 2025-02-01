package com.yz.mall.pms.service.impl;

import com.yz.mall.pms.vo.InternalPmsStockDeductVo;
import com.yz.mall.web.exception.BusinessException;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.feign.InternalPmsStockFeign;
import com.yz.mall.pms.service.InternalPmsStockService;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.enums.CodeEnum;
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
    public Boolean deduct(InternalPmsStockDto deductStock) {
        Result<Boolean> result = feign.deduct(deductStock);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }

    @Override
    public List<InternalPmsStockDeductVo> deductBatch(List<InternalPmsStockDto> productStocks) {
        Result<List<InternalPmsStockDeductVo>> result = feign.deductBatch(productStocks);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
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
