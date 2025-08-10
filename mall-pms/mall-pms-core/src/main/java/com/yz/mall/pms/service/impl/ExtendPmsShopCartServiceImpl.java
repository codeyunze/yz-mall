package com.yz.mall.pms.service.impl;

import com.yz.mall.pms.dto.ExtendPmsCartDto;
import com.yz.mall.pms.dto.ExtendPmsStockDto;
import com.yz.mall.pms.service.ExtendPmsShopCartService;
import com.yz.mall.pms.service.PmsShopCartService;
import com.yz.mall.pms.vo.PmsShopCartSlimVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yunze
 * @date 2025/8/9 星期六 18:55
 */
@Service
public class ExtendPmsShopCartServiceImpl implements ExtendPmsShopCartService {

    private final PmsShopCartService pmsShopCartService;

    public ExtendPmsShopCartServiceImpl(PmsShopCartService pmsShopCartService) {
        this.pmsShopCartService = pmsShopCartService;
    }

    @Override
    public Map<Long, ExtendPmsCartDto> getCartByIds(Long userId, List<Long> cartIds) {
        List<PmsShopCartSlimVo> cartByIds = pmsShopCartService.getCartByIds(userId, cartIds);
        if(CollectionUtils.isEmpty(cartByIds)) {
            return null;
        }

        return cartByIds.stream().collect(Collectors.toMap(PmsShopCartSlimVo::getId, t -> {
            ExtendPmsCartDto dto = new ExtendPmsCartDto();
            BeanUtils.copyProperties(t, dto);
            return dto;
        }));
    }

    @Override
    public boolean removeCartByIds(Long userId, List<Long> cartIds) {
        return pmsShopCartService.removeByIds(cartIds, userId);
    }

    @Override
    public boolean removeCartByProductIds(Long userId, List<ExtendPmsStockDto> products) {
        return pmsShopCartService.removeCartByProductIds(userId, products);
    }
}
