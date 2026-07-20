package com.yz.mall.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.pms.dto.ExtendPmsStockDto;
import com.yz.mall.pms.dto.PmsShopCartAddDto;
import com.yz.mall.pms.dto.PmsShopCartQueryDto;
import com.yz.mall.pms.dto.PmsShopCartUpdateDto;
import com.yz.mall.pms.entity.PmsShopCart;
import com.yz.mall.pms.entity.PmsSku;
import com.yz.mall.pms.enums.ProductPublishStatusEnum;
import com.yz.mall.pms.enums.ProductStatusEnum;
import com.yz.mall.pms.mapper.PmsShopCartMapper;
import com.yz.mall.pms.service.PmsProductService;
import com.yz.mall.pms.service.PmsShopCartService;
import com.yz.mall.pms.service.PmsSkuService;
import com.yz.mall.pms.vo.PmsProductDisplayInfoVo;
import com.yz.mall.pms.vo.PmsShopCartSlimVo;
import com.yz.mall.pms.vo.PmsShopCartVo;
import com.yz.mall.sys.service.ExtendSysFilesService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final PmsSkuService pmsSkuService;
    private final ExtendSysFilesService extendSysFilesService;

    public PmsShopCartServiceImpl(PmsProductService productService
            , PmsSkuService pmsSkuService
            , ExtendSysFilesService extendSysFilesService) {
        this.pmsProductService = productService;
        this.pmsSkuService = pmsSkuService;
        this.extendSysFilesService = extendSysFilesService;
    }

    @Override
    public Long save(PmsShopCartAddDto dto) {
        if (dto.getSkuId() == null) {
            throw new BusinessException("请选择商品规格后再加入购物车");
        }
        PmsSku sku = pmsSkuService.getById(dto.getSkuId());
        if (sku == null) {
            throw new BusinessException("商品规格不存在");
        }
        if (dto.getProductId() == null) {
            dto.setProductId(sku.getProductId());
        } else if (!Objects.equals(dto.getProductId(), sku.getProductId())) {
            throw new BusinessException("商品与规格不匹配");
        }

        int addQuantity = dto.getQuantity() == null || dto.getQuantity() < 1 ? 1 : dto.getQuantity();
        LambdaQueryWrapper<PmsShopCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PmsShopCart::getId, PmsShopCart::getUserId, PmsShopCart::getProductId, PmsShopCart::getSkuId, PmsShopCart::getQuantity);
        queryWrapper.eq(PmsShopCart::getUserId, dto.getUserId());
        queryWrapper.eq(PmsShopCart::getProductId, dto.getProductId());
        queryWrapper.eq(PmsShopCart::getSkuId, dto.getSkuId());
        PmsShopCart cart = baseMapper.selectOne(queryWrapper);
        if (cart != null) {
            cart.setQuantity(cart.getQuantity() + addQuantity);
            baseMapper.updateById(cart);
            return cart.getId();
        }

        PmsShopCart bo = new PmsShopCart();
        BeanUtils.copyProperties(dto, bo);
        bo.setQuantity(addQuantity);
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

    @DS("master")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCartByProductIds(Long userId, List<ExtendPmsStockDto> products) {
        if (CollectionUtils.isEmpty(products)) {
            return true;
        }
        // 优先按 skuId 匹配扣减购物车；无 skuId 时按 productId 汇总
        Map<Long, ExtendPmsStockDto> skuMap = products.stream()
                .filter(item -> item.getSkuId() != null)
                .collect(Collectors.toMap(ExtendPmsStockDto::getSkuId, item -> item, (left, right) -> {
                    left.setQuantity(left.getQuantity() + right.getQuantity());
                    return left;
                }));
        Map<Long, ExtendPmsStockDto> productMap = products.stream()
                .filter(item -> item.getSkuId() == null && item.getProductId() != null)
                .collect(Collectors.toMap(ExtendPmsStockDto::getProductId, item -> item, (left, right) -> {
                    left.setQuantity(left.getQuantity() + right.getQuantity());
                    return left;
                }));

        LambdaQueryWrapper<PmsShopCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PmsShopCart::getUserId, userId);
        if (!skuMap.isEmpty() && !productMap.isEmpty()) {
            queryWrapper.and(w -> w.in(PmsShopCart::getSkuId, skuMap.keySet())
                    .or()
                    .in(PmsShopCart::getProductId, productMap.keySet()));
        } else if (!skuMap.isEmpty()) {
            queryWrapper.in(PmsShopCart::getSkuId, skuMap.keySet());
        } else if (!productMap.isEmpty()) {
            queryWrapper.in(PmsShopCart::getProductId, productMap.keySet());
        } else {
            return true;
        }
        List<PmsShopCart> carts = baseMapper.selectList(queryWrapper);

        List<Long> delCartIds = new ArrayList<>();
        List<PmsShopCart> updateCarts = new ArrayList<>();
        for (PmsShopCart cart : carts) {
            ExtendPmsStockDto stockDto = null;
            if (cart.getSkuId() != null) {
                stockDto = skuMap.get(cart.getSkuId());
            }
            if (stockDto == null) {
                stockDto = productMap.get(cart.getProductId());
            }
            if (stockDto == null) {
                continue;
            }
            if (stockDto.getQuantity() >= cart.getQuantity()) {
                delCartIds.add(cart.getId());
            } else {
                cart.setQuantity(cart.getQuantity() - stockDto.getQuantity());
                updateCarts.add(cart);
            }
        }
        if (!CollectionUtils.isEmpty(delCartIds)) {
            super.removeByIds(delCartIds);
        }
        if (!CollectionUtils.isEmpty(updateCarts)) {
            baseMapper.updateBatchByIds(updateCarts);
        }
        return true;
    }

    @DS("slave")
    @Override
    public Page<PmsShopCartVo> page(PageFilter<PmsShopCartQueryDto> filter) {
        Page<PmsShopCartVo> page = baseMapper.selectPageByFilter(new Page<>(filter.getCurrent(), filter.getSize()), filter.getFilter());
        if (page.getTotal() == 0) {
            return page;
        }
        List<Long> productIds = page.getRecords().stream().map(PmsShopCartVo::getProductId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> skuIds = page.getRecords().stream().map(PmsShopCartVo::getSkuId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, PmsProductDisplayInfoVo> productMap = CollectionUtils.isEmpty(productIds)
                ? Map.of()
                : pmsProductService.getProductDisplayInfoMap(productIds);
        Map<Long, PmsSku> skuMap = CollectionUtils.isEmpty(skuIds)
                ? Map.of()
                : pmsSkuService.listByIds(skuIds).stream().collect(Collectors.toMap(PmsSku::getId, t -> t, (a, b) -> a));

        page.getRecords().forEach(cart -> {
            PmsProductDisplayInfoVo productInfo = productMap.get(cart.getProductId());
            PmsSku sku = cart.getSkuId() == null ? null : skuMap.get(cart.getSkuId());
            cart.setProductStatus(ProductStatusEnum.NORMAL.get());
            if (productInfo == null) {
                cart.setProductStatus(ProductStatusEnum.DELISTING.get());
                cart.setProductName("商品已失效");
                cart.setPrice(BigDecimal.ZERO);
                return;
            }
            if (!ProductPublishStatusEnum.PUBLISH.get().equals(productInfo.getPublishStatus())) {
                cart.setProductStatus(ProductStatusEnum.DELISTING.get());
            } else if (productInfo.getQuantity() == null || productInfo.getQuantity() <= 0) {
                cart.setProductStatus(ProductStatusEnum.SELL_OUT.get());
            }
            cart.setProductName(productInfo.getProductName());
            cart.setRemark(productInfo.getRemark());

            if (sku != null) {
                cart.setSkuName(sku.getSkuName());
                if (sku.getPriceFee() != null) {
                    cart.setPrice(BigDecimal.valueOf(sku.getPriceFee()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                } else {
                    cart.setPrice(productInfo.getProductPrice());
                }
                cart.setAlbumPics(sku.getAlbumPics());
                cart.setPreviewAddress(resolveFirstPreviewUrl(sku.getAlbumPics()));
            } else {
                cart.setPrice(productInfo.getProductPrice());
                cart.setAlbumPics(productInfo.getAlbumPics());
            }
            // SKU 无图或无 SKU 时：优先商品已解析预览图，再回退商品 albumPics
            if (!StringUtils.hasText(cart.getPreviewAddress())) {
                if (!CollectionUtils.isEmpty(productInfo.getProductImages())) {
                    cart.setPreviewAddress(productInfo.getProductImages().get(0));
                } else {
                    cart.setPreviewAddress(resolveFirstPreviewUrl(productInfo.getAlbumPics()));
                    if (!StringUtils.hasText(cart.getAlbumPics())) {
                        cart.setAlbumPics(productInfo.getAlbumPics());
                    }
                }
            }
        });

        return page;
    }

    /**
     * 取 albumPics 第一张图的预览地址
     */
    private String resolveFirstPreviewUrl(String albumPics) {
        if (!StringUtils.hasText(albumPics)) {
            return null;
        }
        String firstId = albumPics.split(",")[0].trim();
        if (!StringUtils.hasText(firstId)) {
            return null;
        }
        try {
            List<String> urls = extendSysFilesService.getFilePreviewByFileIds(List.of(Long.parseLong(firstId)));
            return CollectionUtils.isEmpty(urls) ? null : urls.get(0);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Override
    public List<PmsShopCartSlimVo> getCartByIds(Long userId, List<Long> ids) {
        List<PmsShopCartSlimVo> carts = baseMapper.selectCartByIds(userId, ids);
        if (CollectionUtils.isEmpty(carts)) {
            return null;
        }

        List<Long> productIds = carts.stream().map(PmsShopCartSlimVo::getProductId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> skuIds = carts.stream()
                .map(item -> item.getSkuId() != null ? item.getSkuId() : item.getProductSkuId())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, PmsProductDisplayInfoVo> productDisplayInfoMap = CollectionUtils.isEmpty(productIds)
                ? Map.of()
                : pmsProductService.getProductDisplayInfoMap(productIds);
        Map<Long, PmsSku> skuMap = CollectionUtils.isEmpty(skuIds)
                ? Map.of()
                : pmsSkuService.listByIds(skuIds).stream().collect(Collectors.toMap(PmsSku::getId, t -> t, (a, b) -> a));

        carts.forEach(item -> {
            if (item.getSkuId() == null && item.getProductSkuId() != null) {
                item.setSkuId(item.getProductSkuId());
            }
            if (item.getProductSkuId() == null && item.getSkuId() != null) {
                item.setProductSkuId(item.getSkuId());
            }
            item.setDiscountAmount(BigDecimal.ZERO);
            PmsSku sku = item.getSkuId() == null ? null : skuMap.get(item.getSkuId());
            if (sku != null && sku.getPriceFee() != null) {
                BigDecimal price = BigDecimal.valueOf(sku.getPriceFee()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                item.setRealAmount(price.subtract(item.getDiscountAmount()));
                return;
            }
            PmsProductDisplayInfoVo vo = productDisplayInfoMap.get(item.getProductId());
            if (vo != null && vo.getProductPrice() != null) {
                item.setRealAmount(vo.getProductPrice().subtract(item.getDiscountAmount()));
            } else {
                item.setRealAmount(BigDecimal.ZERO);
            }
        });
        return carts;
    }
}
