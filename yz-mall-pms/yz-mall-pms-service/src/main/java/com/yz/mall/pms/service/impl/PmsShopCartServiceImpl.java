package com.yz.mall.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.pms.dto.PmsShopCartAddDto;
import com.yz.mall.pms.dto.PmsShopCartQueryDto;
import com.yz.mall.pms.dto.PmsShopCartUpdateDto;
import com.yz.mall.pms.entity.PmsShopCart;
import com.yz.mall.pms.enums.ProductPublishStatusEnum;
import com.yz.mall.pms.enums.ProductStatusEnum;
import com.yz.mall.pms.mapper.PmsShopCartMapper;
import com.yz.mall.pms.service.PmsProductService;
import com.yz.mall.pms.service.PmsShopCartService;
import com.yz.mall.pms.vo.PmsProductDisplayInfoVo;
import com.yz.mall.pms.vo.PmsShopCartSlimVo;
import com.yz.mall.pms.vo.PmsShopCartVo;
import com.yz.mall.web.common.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车数据表(PmsShopCart)表服务实现类
 *
 * @author yunze
 * @since 2025-01-24 10:08:18
 */
@Service
public class PmsShopCartServiceImpl extends ServiceImpl<PmsShopCartMapper, PmsShopCart> implements PmsShopCartService {

    private final PmsProductService pmsProductService;

    public PmsShopCartServiceImpl(PmsProductService productService) {
        this.pmsProductService = productService;
    }

    @Override
    public Long save(PmsShopCartAddDto dto) {
        LambdaQueryWrapper<PmsShopCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PmsShopCart::getId, PmsShopCart::getUserId, PmsShopCart::getProductId, PmsShopCart::getQuantity);
        queryWrapper.eq(PmsShopCart::getUserId, dto.getUserId());
        queryWrapper.eq(PmsShopCart::getProductId, dto.getProductId());
        PmsShopCart cart = baseMapper.selectOne(queryWrapper);
        if (cart != null) {
            cart.setQuantity(cart.getQuantity() + 1);
            baseMapper.updateById(cart);
            return cart.getId();
        }

        PmsShopCart bo = new PmsShopCart();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(PmsShopCartUpdateDto dto) {
        PmsShopCart bo = new PmsShopCart();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Transactional
    @Override
    public boolean removeByIds(List<Long> ids, Long userId) {
        LambdaUpdateWrapper<PmsShopCart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(PmsShopCart::getId, ids);
        updateWrapper.eq(PmsShopCart::getUserId, userId);
        return baseMapper.delete(updateWrapper) > 0;
    }

    @DS("slave")
    @Override
    public Page<PmsShopCartVo> page(PageFilter<PmsShopCartQueryDto> filter) {
        Page<PmsShopCartVo> page = baseMapper.selectPageByFilter(new Page<>(filter.getCurrent(), filter.getSize()), filter.getFilter());
        if (page.getTotal() == 0) {
            return page;
        }
        List<Long> productIds = page.getRecords().stream().map(PmsShopCartVo::getProductId).collect(Collectors.toList());
        Map<Long, PmsProductDisplayInfoVo> productMap = pmsProductService.getProductDisplayInfoMap(productIds);
        page.getRecords().forEach(cart -> {
            PmsProductDisplayInfoVo productInfo = productMap.get(cart.getProductId());
            // 商品情况正常
            cart.setProductStatus(ProductStatusEnum.NORMAL.get());
            if (!ProductPublishStatusEnum.PUBLISH.get().equals(productInfo.getPublishStatus())) {
                // 商品下架
                cart.setProductStatus(ProductStatusEnum.DELISTING.get());
            } else if(productInfo.getQuantity() <= 0) {
                // 商品无货
                cart.setProductStatus(ProductStatusEnum.SELL_OUT.get());
            }
            cart.setProductName(productInfo.getProductName());
            cart.setPrice(productInfo.getProductPrice());
            cart.setRemark(productInfo.getRemark());
            cart.setAlbumPics(productInfo.getAlbumPics());

            if (!CollectionUtils.isEmpty(productInfo.getProductImages())) {
                cart.setPreviewAddress(productInfo.getProductImages().get(0));
            }
        });

        return page;
    }

    @Override
    public List<PmsShopCartSlimVo> getCartByIds(Long userId, List<Long> ids) {
        List<PmsShopCartSlimVo> carts = baseMapper.selectCartByIds(userId, ids);
        if (CollectionUtils.isEmpty(carts)) {
            return null;
        }

        // 查询商品的价格
        // TODO: 2025/2/5 yunze 可以将商品信息调整到缓存里面去查询
        List<Long> productIds = carts.stream().map(PmsShopCartSlimVo::getProductId).collect(Collectors.toList());
        Map<Long, PmsProductDisplayInfoVo> productDisplayInfoMap = pmsProductService.getProductDisplayInfoMap(productIds);
        carts.forEach(item -> {
            item.setDiscountAmount(BigDecimal.ZERO);
            PmsProductDisplayInfoVo vo = productDisplayInfoMap.get(item.getProductId());
            item.setRealAmount(vo.getProductPrice().subtract(item.getDiscountAmount()));
        });
        return carts;
    }
}

