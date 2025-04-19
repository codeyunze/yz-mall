package com.yz.mall.pms.impl;

import com.yz.mall.pms.dto.InternalPmsCartDto;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.service.InternalPmsShopCartService;
import com.yz.mall.pms.service.PmsShopCartService;
import com.yz.mall.pms.vo.PmsShopCartSlimVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内部暴露service实现类: 购物车
 *
 * @author yunze
 * @date 2025/2/4 16:01
 */
@Service
public class InternalPmsShopCartServiceImpl implements InternalPmsShopCartService {

    private final PmsShopCartService pmsShopCartService;

    public InternalPmsShopCartServiceImpl(PmsShopCartService pmsShopCartService) {
        this.pmsShopCartService = pmsShopCartService;
    }

    @Override
    public Map<Long, InternalPmsCartDto> getCartByIds(Long userId, List<Long> cartIds) {
        List<PmsShopCartSlimVo> cartByIds = pmsShopCartService.getCartByIds(userId, cartIds);
        if (CollectionUtils.isEmpty(cartByIds)) {
            return null;
        }
        return cartByIds.stream().collect(Collectors.toMap(PmsShopCartSlimVo::getId, t -> {
            InternalPmsCartDto internalPmsCartDto = new InternalPmsCartDto();
            BeanUtils.copyProperties(t, internalPmsCartDto);
            return internalPmsCartDto;
        }));
    }

    @Override
    public boolean removeCartByIds(Long userId, List<Long> cartIds) {
        return pmsShopCartService.removeByIds(cartIds, userId);
    }

    @Override
    public boolean removeCartByProductIds(Long userId, List<InternalPmsStockDto> products) {
        return pmsShopCartService.removeCartByProductIds(userId, products);
    }
}
