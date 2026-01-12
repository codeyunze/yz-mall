package com.yz.mall.pms.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.pms.dto.PmsStockInDetailAddDto;
import com.yz.mall.pms.dto.PmsStockInDetailQueryDto;
import com.yz.mall.pms.dto.PmsStockInDetailUpdateDto;
import com.yz.mall.pms.entity.PmsSku;
import com.yz.mall.pms.entity.PmsStockInDetail;
import com.yz.mall.pms.mapper.PmsStockInDetailMapper;
import com.yz.mall.pms.entity.PmsCategory;
import com.yz.mall.pms.service.PmsCategoryService;
import com.yz.mall.pms.service.PmsProductQueryService;
import com.yz.mall.pms.service.PmsSkuService;
import com.yz.mall.pms.service.PmsStockInDetailService;
import com.yz.mall.pms.vo.PmsProductSlimVo;
import com.yz.mall.pms.vo.PmsSkuVo;
import com.yz.mall.pms.vo.PmsStockInDetailVo;
import com.yz.mall.serial.service.ExtendSerialService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 产品管理-商品入库日志表(PmsStockInDetail)表服务实现类
 *
 * @author yunze
 * @since 2024-12-25 19:53:27
 */
@Service
public class PmsStockInDetailServiceImpl extends ServiceImpl<PmsStockInDetailMapper, PmsStockInDetail> implements PmsStockInDetailService {

    private final ExtendSerialService extendSerialService;

    private final PmsProductQueryService pmsProductQueryService;

    private final PmsSkuService pmsSkuService;

    private final PmsCategoryService pmsCategoryService;

    public PmsStockInDetailServiceImpl(ExtendSerialService extendSerialService, PmsProductQueryService pmsProductQueryService, PmsSkuService pmsSkuService, PmsCategoryService pmsCategoryService) {
        this.extendSerialService = extendSerialService;
        this.pmsProductQueryService = pmsProductQueryService;
        this.pmsSkuService = pmsSkuService;
        this.pmsCategoryService = pmsCategoryService;
    }

    @Transactional
    @Override
    public Long in(PmsStockInDetailAddDto dto) {
        PmsStockInDetail bo = new PmsStockInDetail();
        bo.setId(IdUtil.getSnowflakeNextId());
        String prefix = "RK" + LocalDateTimeUtil.format(LocalDate.now(), DatePattern.PURE_DATE_PATTERN);
        bo.setStockInCode(extendSerialService.generateNumber(prefix, 6));
        bo.setRemark(dto.getRemark());
        bo.setProductId(dto.getProductId());
        bo.setSkuId(dto.getSkuId());
        bo.setQuantity(dto.getQuantity());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(PmsStockInDetailUpdateDto dto) {
        PmsStockInDetail bo = new PmsStockInDetail();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<PmsStockInDetailVo> page(PageFilter<PmsStockInDetailQueryDto> filter) {
        PmsStockInDetailQueryDto queryFilter = filter.getFilter();
        
        // 构建查询条件
        LambdaQueryWrapper<PmsStockInDetail> queryWrapper = buildQueryWrapper(queryFilter);
        if (queryWrapper == null) {
            return new Page<>();
        }
        
        // 执行分页查询
        Page<PmsStockInDetail> stockInPage = baseMapper.selectPage(
                new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
        if (stockInPage.getTotal() == 0) {
            return new Page<>();
        }

        // 提取商品ID和SKU ID
        List<Long> productIds = extractProductIds(stockInPage.getRecords());
        List<Long> skuIds = extractSkuIds(stockInPage.getRecords());

        // 查询商品和SKU信息
        List<PmsProductSlimVo> products = queryProducts(productIds);
        List<PmsSkuVo> skuList = querySkus(skuIds);

        if (CollectionUtils.isEmpty(products) && CollectionUtils.isEmpty(skuList)) {
            return new Page<>();
        }

        // 查询商品分类信息
        Map<Long, PmsCategory> categoryMap = queryCategories(products);

        // 组装返回结果
        return buildResultPage(stockInPage, products, skuList, categoryMap);
    }

    /**
     * 构建查询条件
     *
     * @param queryFilter 查询过滤条件
     * @return 查询条件包装器，如果过滤条件导致无结果则返回null
     */
    private LambdaQueryWrapper<PmsStockInDetail> buildQueryWrapper(PmsStockInDetailQueryDto queryFilter) {
        LambdaQueryWrapper<PmsStockInDetail> queryWrapper = new LambdaQueryWrapper<>();
        
        // 基础查询条件
        queryWrapper.eq(StringUtils.hasText(queryFilter.getStockInCode()), 
                PmsStockInDetail::getStockInCode, queryFilter.getStockInCode());
        queryWrapper.eq(queryFilter.getSkuId() != null && queryFilter.getSkuId() != 0, 
                PmsStockInDetail::getSkuId, queryFilter.getSkuId());
        
        // SKU名称过滤
        if (StringUtils.hasText(queryFilter.getSkuName())) {
            List<Long> skuIds = findSkuIdsByName(queryFilter.getSkuName());
            if (CollectionUtils.isEmpty(skuIds)) {
                return null; // 没有匹配的SKU，返回null表示无结果
            }
            queryWrapper.in(PmsStockInDetail::getSkuId, skuIds);
        }
        
        // 商品分类过滤
        if (queryFilter.getCategoryId() != null && queryFilter.getCategoryId() != 0) {
            List<Long> productIds = findProductIdsByCategoryId(queryFilter.getCategoryId());
            if (CollectionUtils.isEmpty(productIds)) {
                return null; // 没有匹配的商品，返回null表示无结果
            }
            queryWrapper.in(PmsStockInDetail::getProductId, productIds);
        }
        
        queryWrapper.orderByDesc(PmsStockInDetail::getId);
        return queryWrapper;
    }

    /**
     * 根据SKU名称查找SKU ID列表
     *
     * @param skuName SKU名称
     * @return SKU ID列表
     */
    private List<Long> findSkuIdsByName(String skuName) {
        LambdaQueryWrapper<PmsSku> skuQueryWrapper = new LambdaQueryWrapper<>();
        skuQueryWrapper.like(PmsSku::getSkuName, skuName);
        List<PmsSku> skuList = pmsSkuService.list(skuQueryWrapper);
        return skuList.stream()
                .map(PmsSku::getId)
                .collect(Collectors.toList());
    }

    /**
     * 根据分类 ID 查找商品 ID 列表
     *
     * @param categoryId 分类 ID
     * @return 商品 ID列表
     */
    private List<Long> findProductIdsByCategoryId(Long categoryId) {
        LambdaQueryWrapper<com.yz.mall.pms.entity.PmsProduct> productQueryWrapper = new LambdaQueryWrapper<>();
        productQueryWrapper.eq(com.yz.mall.pms.entity.PmsProduct::getCategoryId, categoryId);
        List<com.yz.mall.pms.entity.PmsProduct> productList = pmsProductQueryService.list(productQueryWrapper);
        return productList.stream()
                .map(com.yz.mall.pms.entity.PmsProduct::getId)
                .collect(Collectors.toList());
    }

    /**
     * 从入库明细记录中提取商品 ID 列表
     *
     * @param records 入库明细记录列表
     * @return 商品 ID 列表
     */
    private List<Long> extractProductIds(List<PmsStockInDetail> records) {
        return records.stream()
                .map(PmsStockInDetail::getProductId)
                .filter(id -> id != null && id != 0)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 从入库明细记录中提取SKU ID列表
     *
     * @param records 入库明细记录列表
     * @return SKU ID列表
     */
    private List<Long> extractSkuIds(List<PmsStockInDetail> records) {
        return records.stream()
                .map(PmsStockInDetail::getSkuId)
                .filter(id -> id != null && id != 0)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 查询商品信息
     *
     * @param productIds 商品ID列表
     * @return 商品信息列表
     */
    private List<PmsProductSlimVo> queryProducts(List<Long> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            return new ArrayList<>();
        }
        return pmsProductQueryService.getProductByProductIds(productIds);
    }

    /**
     * 查询SKU信息
     *
     * @param skuIds SKU ID列表
     * @return SKU信息列表
     */
    private List<PmsSkuVo> querySkus(List<Long> skuIds) {
        if (CollectionUtils.isEmpty(skuIds)) {
            return new ArrayList<>();
        }
        List<PmsSku> skuEntities = pmsSkuService.listByIds(skuIds);
        if (CollectionUtils.isEmpty(skuEntities)) {
            return new ArrayList<>();
        }
        return skuEntities.stream()
                .map(sku -> {
                    PmsSkuVo vo = new PmsSkuVo();
                    BeanUtils.copyProperties(sku, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 查询商品分类信息
     *
     * @param products 商品信息列表
     * @return 分类ID到分类实体的映射
     */
    private Map<Long, PmsCategory> queryCategories(List<PmsProductSlimVo> products) {
        List<Long> categoryIds = products.stream()
                .map(PmsProductSlimVo::getCategoryId)
                .filter(id -> id != null && id != 0)
                .distinct()
                .collect(Collectors.toList());
        
        if (CollectionUtils.isEmpty(categoryIds)) {
            return new java.util.HashMap<>();
        }
        
        List<PmsCategory> categories = pmsCategoryService.listByIds(categoryIds);
        if (CollectionUtils.isEmpty(categories)) {
            return new java.util.HashMap<>();
        }
        
        return categories.stream()
                .collect(Collectors.toMap(PmsCategory::getId, t -> t));
    }

    /**
     * 构建返回结果页面
     *
     * @param stockInPage 入库明细分页数据
     * @param products 商品信息列表
     * @param skuList SKU信息列表
     * @param categoryMap 分类映射
     * @return 入库明细VO分页数据
     */
    private Page<PmsStockInDetailVo> buildResultPage(
            Page<PmsStockInDetail> stockInPage,
            List<PmsProductSlimVo> products,
            List<PmsSkuVo> skuList,
            Map<Long, PmsCategory> categoryMap) {
        
        Page<PmsStockInDetailVo> page = new Page<>();
        page.setTotal(stockInPage.getTotal());
        
        // 构建映射表
        Map<Long, PmsProductSlimVo> productMap = products.stream()
                .collect(Collectors.toMap(PmsProductSlimVo::getId, t -> t));
        Map<Long, PmsSkuVo> skuMap = skuList.stream()
                .collect(Collectors.toMap(PmsSkuVo::getId, t -> t));
        
        // 组装VO数据
        List<PmsStockInDetailVo> voList = stockInPage.getRecords().stream()
                .map(detail -> buildStockInDetailVo(detail, productMap, skuMap, categoryMap))
                .collect(Collectors.toList());
        
        page.setRecords(voList);
        return page;
    }

    /**
     * 构建入库明细VO对象
     *
     * @param detail 入库明细实体
     * @param productMap 商品映射
     * @param skuMap SKU映射
     * @param categoryMap 分类映射
     * @return 入库明细VO
     */
    private PmsStockInDetailVo buildStockInDetailVo(
            PmsStockInDetail detail,
            Map<Long, PmsProductSlimVo> productMap,
            Map<Long, PmsSkuVo> skuMap,
            Map<Long, PmsCategory> categoryMap) {
        
        PmsStockInDetailVo vo = new PmsStockInDetailVo();
        BeanUtils.copyProperties(detail, vo);
        
        // 设置商品信息
        if (detail.getProductId() != null) {
            PmsProductSlimVo productSlimVo = productMap.get(detail.getProductId());
            if (productSlimVo != null) {
                vo.setProductName(productSlimVo.getProductName());
                vo.setTitles(productSlimVo.getTitles());
                vo.setAlbumPics(productSlimVo.getAlbumPics());
                vo.setCategoryId(productSlimVo.getCategoryId());
                
                // 设置商品分类名称
                if (productSlimVo.getCategoryId() != null) {
                    PmsCategory category = categoryMap.get(productSlimVo.getCategoryId());
                    if (category != null) {
                        vo.setCategoryName(category.getCategoryName());
                    }
                }
            }
        }
        
        // 设置SKU信息
        if (detail.getSkuId() != null) {
            PmsSkuVo skuVo = skuMap.get(detail.getSkuId());
            if (skuVo != null) {
                vo.setSkuCode(skuVo.getSkuCode());
                vo.setSkuName(skuVo.getSkuName());
            }
        }
        
        return vo;
    }
}

