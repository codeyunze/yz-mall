package com.yz.mall.pms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.pms.dto.PmsSkuAddDto;
import com.yz.mall.pms.dto.PmsSkuQueryDto;
import com.yz.mall.pms.dto.PmsSkuUpdateDto;
import com.yz.mall.pms.entity.PmsSku;
import com.yz.mall.pms.mapper.PmsSkuMapper;
import com.yz.mall.pms.service.PmsSkuService;
import com.yz.mall.pms.service.PmsProductQueryService;
import com.yz.mall.pms.vo.PmsSkuVo;
import com.yz.mall.pms.vo.PmsProductSlimVo;
import com.yz.mall.base.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品SKU表(PmsSku)表服务实现类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Service
public class PmsSkuServiceImpl extends ServiceImpl<PmsSkuMapper, PmsSku> implements PmsSkuService {

    private final PmsProductQueryService pmsProductQueryService;

    public PmsSkuServiceImpl(PmsProductQueryService pmsProductQueryService) {
        this.pmsProductQueryService = pmsProductQueryService;
    }

    @Transactional
    @Override
    public Long save(PmsSkuAddDto dto) {
        PmsSku bo = new PmsSku();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setCreateId(StpUtil.getLoginIdAsLong());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(PmsSkuUpdateDto dto) {
        PmsSku bo = new PmsSku();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<PmsSkuVo> page(PageFilter<PmsSkuQueryDto> filter) {
        PmsSkuQueryDto query = filter.getFilter();
        LambdaQueryWrapper<PmsSku> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(query.getProductId() != null, PmsSku::getProductId, query.getProductId());
        queryWrapper.like(StringUtils.hasText(query.getSkuCode()), PmsSku::getSkuCode, query.getSkuCode());

        queryWrapper.orderByDesc(PmsSku::getId);
        Page<PmsSku> skuPage = baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
        
        if (skuPage.getTotal() == 0) {
            return new Page<>();
        }

        // 提取所有商品 ID
        List<Long> productIds = skuPage.getRecords().stream()
                .map(PmsSku::getProductId)
                .filter(id -> id != null && id != 0)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询商品信息
        Map<Long, String> productNameMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(productIds)) {
            List<PmsProductSlimVo> products = pmsProductQueryService.getProductByProductIds(productIds);
            if (!CollectionUtils.isEmpty(products)) {
                productNameMap = products.stream()
                        .collect(Collectors.toMap(PmsProductSlimVo::getId, PmsProductSlimVo::getProductName, (k1, k2) -> k1));
            }
        }

        // 转换为 VO 并填充商品名称
        Page<PmsSkuVo> voPage = new Page<>();
        voPage.setTotal(skuPage.getTotal());
        Map<Long, String> finalProductNameMap = productNameMap;
        List<PmsSkuVo> voList = skuPage.getRecords().stream().map(sku -> {
            PmsSkuVo vo = new PmsSkuVo();
            BeanUtils.copyProperties(sku, vo);
            vo.setProductName(finalProductNameMap.getOrDefault(sku.getProductId(), ""));
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public PmsSkuVo detail(Long id) {
        PmsSku bo = baseMapper.selectById(id);
        PmsSkuVo vo = new PmsSkuVo();
        BeanUtils.copyProperties(bo, vo);
        return vo;
    }

    @Override
    public List<PmsSkuVo> listByProductId(Long productId) {
        LambdaQueryWrapper<PmsSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PmsSku::getProductId, productId);
        queryWrapper.orderByDesc(PmsSku::getId);
        List<PmsSku> list = baseMapper.selectList(queryWrapper);
        return list.stream().map(bo -> {
            PmsSkuVo vo = new PmsSkuVo();
            BeanUtils.copyProperties(bo, vo);
            return vo;
        }).collect(Collectors.toList());
    }

}
