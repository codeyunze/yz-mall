package com.yz.mall.pms.impl;

import com.yz.mall.pms.dto.InternalPmsProductSlimVo;
import com.yz.mall.pms.service.InternalPmsProductService;
import com.yz.mall.pms.service.PmsProductExpandService;
import com.yz.mall.pms.vo.PmsProductSlimVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 内部开放接口实现类: 商品信息
 *
 * @author yunze
 * @since 2025/3/16 18:59
 */
@Service
public class InternalPmsProductServiceImpl implements InternalPmsProductService {

    private final PmsProductExpandService service;

    public InternalPmsProductServiceImpl(PmsProductExpandService service) {
        this.service = service;
    }

    @Override
    public List<InternalPmsProductSlimVo> getProductByProductIds(List<Long> productIds) {
        List<PmsProductSlimVo> products = service.getProductByProductIds(productIds);
        if (CollectionUtils.isEmpty(products)) {
            return Collections.emptyList();
        }
        return products.stream().map(t -> {
            InternalPmsProductSlimVo vo = new InternalPmsProductSlimVo();
            BeanUtils.copyProperties(t, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
