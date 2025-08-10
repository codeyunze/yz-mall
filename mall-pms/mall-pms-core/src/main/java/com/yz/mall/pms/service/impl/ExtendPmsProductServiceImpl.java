package com.yz.mall.pms.service.impl;

import com.yz.mall.pms.dto.ExtendPmsProductSlimVo;
import com.yz.mall.pms.service.ExtendPmsProductService;
import com.yz.mall.pms.service.PmsProductService;
import com.yz.mall.pms.vo.PmsProductDisplayInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author yunze
 * @date 2025/8/9 星期六 18:46
 */
@Service
public class ExtendPmsProductServiceImpl implements ExtendPmsProductService {

    private final PmsProductService pmsProductService;

    public ExtendPmsProductServiceImpl(PmsProductService pmsProductService) {
        this.pmsProductService = pmsProductService;
    }

    @Override
    public List<ExtendPmsProductSlimVo> getProductByProductIds(List<Long> productIds) {
        Map<Long, PmsProductDisplayInfoVo> infoMap = pmsProductService.getProductDisplayInfoMap(productIds);
        if (CollectionUtils.isEmpty(infoMap)) {
            return null;
        }
        return infoMap.values().stream().map(t -> {
            ExtendPmsProductSlimVo vo = new ExtendPmsProductSlimVo();
            BeanUtils.copyProperties(t, vo);
            return vo;
        }).toList();
    }

}
