package com.yz.mall.pms.service.impl;

import com.yz.mall.pms.dto.InternalPmsCartDto;
import com.yz.mall.pms.feign.InternalPmsShopCartFeign;
import com.yz.mall.pms.service.InternalPmsShopCartService;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.enums.CodeEnum;
import com.yz.mall.web.exception.BusinessException;
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
public class InternalPmsShopCartServiceImpl implements InternalPmsShopCartService {

    private final InternalPmsShopCartFeign internalPmsShopCartFeign;

    public InternalPmsShopCartServiceImpl(InternalPmsShopCartFeign internalPmsShopCartFeign) {
        this.internalPmsShopCartFeign = internalPmsShopCartFeign;
    }

    @Override
    public Map<Long, InternalPmsCartDto> getCartByIds(Long userId, List<Long> cartIds) {
        Result<Map<Long, InternalPmsCartDto>> result = internalPmsShopCartFeign.getCartByIds(cartIds);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }

    @Override
    public boolean removeCartByIds(Long userId, List<Long> cartIds) {
        Result<Boolean> result = internalPmsShopCartFeign.removeCartByIds(cartIds);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }
}
