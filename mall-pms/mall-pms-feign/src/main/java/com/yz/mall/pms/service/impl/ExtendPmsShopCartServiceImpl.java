package com.yz.mall.pms.service.impl;

import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.pms.dto.ExtendPmsCartDto;
import com.yz.mall.pms.dto.ExtendPmsStockDto;
import com.yz.mall.pms.feign.ExtendPmsShopCartFeign;
import com.yz.mall.pms.service.ExtendPmsShopCartService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 内部暴露service实现类: 购物车
 *
 * @author yunze
 * @date 2025/2/4 16:01
 */
@Service
public class ExtendPmsShopCartServiceImpl implements ExtendPmsShopCartService {

    private final ExtendPmsShopCartFeign extendPmsShopCartFeign;

    public ExtendPmsShopCartServiceImpl(ExtendPmsShopCartFeign extendPmsShopCartFeign) {
        this.extendPmsShopCartFeign = extendPmsShopCartFeign;
    }

    @Override
    public Map<Long, ExtendPmsCartDto> getCartByIds(Long userId, List<Long> cartIds) {
        Result<Map<Long, ExtendPmsCartDto>> result = extendPmsShopCartFeign.getCartByIds(cartIds);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }

    @Override
    public boolean removeCartByIds(Long userId, List<Long> cartIds) {
        Result<Boolean> result = extendPmsShopCartFeign.removeCartByIds(cartIds);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }

    @Override
    public boolean removeCartByProductIds(Long userId, List<ExtendPmsStockDto> products) {
        Result<Boolean> result = extendPmsShopCartFeign.removeCartByProductIds(products);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }
}
