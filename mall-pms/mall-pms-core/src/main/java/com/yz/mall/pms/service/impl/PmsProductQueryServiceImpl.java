package com.yz.mall.pms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.pms.entity.PmsProduct;
import com.yz.mall.pms.mapper.PmsProductMapper;
import com.yz.mall.pms.service.PmsProductQueryService;
import com.yz.mall.pms.vo.PmsProductSlimVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品表(PmsProduct)表服务实现类
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Service
public class PmsProductQueryServiceImpl extends ServiceImpl<PmsProductMapper, PmsProduct> implements PmsProductQueryService {

    @Override
    public List<PmsProductSlimVo> getProductByProductIds(List<Long> productIds) {
        LambdaQueryWrapper<PmsProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PmsProduct::getId, PmsProduct::getProductName, PmsProduct::getTitles
                , PmsProduct::getAlbumPics, PmsProduct::getProductPrice);
        queryWrapper.in(PmsProduct::getId, productIds);
        List<PmsProduct> products = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(products)) {
            return Collections.emptyList();
        }

        return products.stream().map(PmsProductSlimVo::new).collect(Collectors.toList());
    }
}