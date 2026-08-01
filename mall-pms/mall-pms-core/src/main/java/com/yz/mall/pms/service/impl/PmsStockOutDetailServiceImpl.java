package com.yz.mall.pms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.pms.dto.ExtendPmsStockDto;
import com.yz.mall.pms.dto.PmsStockOutDetailAddDto;
import com.yz.mall.pms.dto.PmsStockOutDetailQueryDto;
import com.yz.mall.pms.dto.PmsStockOutDetailUpdateDto;
import com.yz.mall.pms.entity.PmsSku;
import com.yz.mall.pms.entity.PmsStockOutDetail;
import com.yz.mall.pms.mapper.PmsStockOutDetailMapper;
import com.yz.mall.pms.entity.PmsCategory;
import com.yz.mall.pms.service.PmsCategoryService;
import com.yz.mall.pms.service.PmsProductQueryService;
import com.yz.mall.pms.service.PmsSkuService;
import com.yz.mall.pms.service.PmsStockOutDetailService;
import com.yz.mall.pms.vo.PmsProductSlimVo;
import com.yz.mall.pms.vo.PmsSkuVo;
import com.yz.mall.pms.vo.PmsStockOutDetailVo;
import com.yz.mall.base.IdsDto;
import com.yz.mall.base.PageFilter;
import com.yz.mall.serial.service.ExtendSerialService;
import com.yz.mall.sys.service.ExtendSysUserService;
import com.yz.mall.sys.vo.ExtendSysUserSlimVo;
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
 * 产品管理-商品出库日志表(PmsStockOutDetail)表服务实现类
 *
 * @author yunze
 * @since 2024-12-27 12:50:27
 */
@Service
public class PmsStockOutDetailServiceImpl extends ServiceImpl<PmsStockOutDetailMapper, PmsStockOutDetail> implements PmsStockOutDetailService {

    private final ExtendSerialService extendSerialService;

    private final PmsProductQueryService pmsProductQueryService;

    private final PmsSkuService skuService;

    private final PmsCategoryService pmsCategoryService;

    private final ExtendSysUserService extendSysUserService;

    public PmsStockOutDetailServiceImpl(ExtendSerialService extendSerialService
            , PmsProductQueryService pmsProductQueryService
            , PmsSkuService skuService
            , PmsCategoryService pmsCategoryService
            , ExtendSysUserService extendSysUserService) {
        this.extendSerialService = extendSerialService;
        this.pmsProductQueryService = pmsProductQueryService;
        this.skuService = skuService;
        this.pmsCategoryService = pmsCategoryService;
        this.extendSysUserService = extendSysUserService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long out(PmsStockOutDetailAddDto dto) {
        PmsStockOutDetail bo = new PmsStockOutDetail();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        String prefix = "CK" + LocalDateTimeUtil.format(LocalDate.now(), DatePattern.PURE_DATE_PATTERN);
        bo.setStockOutCode(extendSerialService.generateNumber(prefix, 6));
        bo.setCreateId(StpUtil.getLoginIdAsLong());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean outBatch(List<ExtendPmsStockDto> outDetails) {
        String prefix = "CK" + LocalDateTimeUtil.format(LocalDate.now(), DatePattern.PURE_DATE_PATTERN);
        String stockOutCode = extendSerialService.generateNumber(prefix, 6);
        List<PmsStockOutDetail> outDetailList = new ArrayList<>();
        Long createId = StpUtil.getLoginIdAsLong();
        outDetails.forEach(out -> {
            PmsStockOutDetail bo = new PmsStockOutDetail();
            bo.setId(IdUtil.getSnowflakeNextId());
            bo.setStockOutCode(stockOutCode);
            bo.setSkuId(out.getSkuId());
            bo.setQuantity(out.getQuantity());
            bo.setOrderId(out.getOrderId());
            bo.setCreateId(createId);
            // 根据 skuId 获取 productId
            Long productId = out.getProductId();
            if (productId == null && out.getSkuId() != null) {
                PmsSku sku = skuService.getById(out.getSkuId());
                if (sku != null) {
                    productId = sku.getProductId();
                }
            }
            bo.setProductId(productId);
            outDetailList.add(bo);
        });
        return super.saveBatch(outDetailList);
    }

    @Override
    public boolean update(PmsStockOutDetailUpdateDto dto) {
        PmsStockOutDetail bo = new PmsStockOutDetail();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<PmsStockOutDetailVo> page(PageFilter<PmsStockOutDetailQueryDto> filter) {
        PmsStockOutDetailQueryDto queryFilter = filter.getFilter();
        
        // 构建查询条件
        LambdaQueryWrapper<PmsStockOutDetail> queryWrapper = buildQueryWrapper(queryFilter);
        
        // 执行分页查询
        Page<PmsStockOutDetail> stockOutPage = baseMapper.selectPage(
                new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
        if (stockOutPage.getTotal() == 0) {
            return new Page<>();
        }

        // 提取商品ID、SKU ID、userId
        List<Long> productIds = extractProductIds(stockOutPage.getRecords());
        List<Long> skuIds = extractSkuIds(stockOutPage.getRecords());
        List<Long> userIds = stockOutPage.getRecords().stream()
                .map(PmsStockOutDetail::getCreateId)
                .filter(id -> id != null && id != 0)
                .distinct()
                .collect(Collectors.toList());

        // 查询商品、SKU 和用户信息
        List<PmsProductSlimVo> products = queryProducts(productIds);
        List<PmsSkuVo> skuList = querySkus(skuIds);
        Map<Long, ExtendSysUserSlimVo> userMap = extendSysUserService.getUserSlimByIds(new IdsDto<>(userIds));

        if (CollectionUtils.isEmpty(products) && CollectionUtils.isEmpty(skuList)) {
            return new Page<>();
        }

        // 查询商品分类信息
        Map<Long, PmsCategory> categoryMap = queryCategories(products);

        // 组装返回结果
        return buildResultPage(stockOutPage, products, skuList, categoryMap, userMap);
    }

    /**
     * 构建查询条件
     *
     * @param queryFilter 查询过滤条件
     * @return 查询条件包装器
     */
    private LambdaQueryWrapper<PmsStockOutDetail> buildQueryWrapper(PmsStockOutDetailQueryDto queryFilter) {
        LambdaQueryWrapper<PmsStockOutDetail> queryWrapper = new LambdaQueryWrapper<>();
        
        // 基础查询条件
        queryWrapper.eq(StringUtils.hasText(queryFilter.getStockOutCode()), 
                PmsStockOutDetail::getStockOutCode, queryFilter.getStockOutCode());
        queryWrapper.eq(queryFilter.getProductId() != null && queryFilter.getProductId() != 0, 
                PmsStockOutDetail::getProductId, queryFilter.getProductId());
        queryWrapper.eq(queryFilter.getOrderId() != null && queryFilter.getOrderId() != 0, 
                PmsStockOutDetail::getOrderId, queryFilter.getOrderId());
        
        queryWrapper.orderByDesc(PmsStockOutDetail::getId);
        return queryWrapper;
    }

    /**
     * 从出库明细记录中提取商品 ID 列表
     *
     * @param records 出库明细记录列表
     * @return 商品 ID 列表
     */
    private List<Long> extractProductIds(List<PmsStockOutDetail> records) {
        return records.stream()
                .map(PmsStockOutDetail::getProductId)
                .filter(id -> id != null && id != 0)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 从出库明细记录中提取SKU ID列表
     *
     * @param records 出库明细记录列表
     * @return SKU ID列表
     */
    private List<Long> extractSkuIds(List<PmsStockOutDetail> records) {
        return records.stream()
                .map(PmsStockOutDetail::getSkuId)
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
        List<PmsSku> skuEntities = skuService.listByIds(skuIds);
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
     * @param stockOutPage 出库明细分页数据
     * @param products 商品信息列表
     * @param skuList SKU信息列表
     * @param categoryMap 分类映射
     * @param userMap 用户映射
     * @return 出库明细VO分页数据
     */
    private Page<PmsStockOutDetailVo> buildResultPage(
            Page<PmsStockOutDetail> stockOutPage,
            List<PmsProductSlimVo> products,
            List<PmsSkuVo> skuList,
            Map<Long, PmsCategory> categoryMap,
            Map<Long, ExtendSysUserSlimVo> userMap) {
        
        Page<PmsStockOutDetailVo> page = new Page<>();
        page.setTotal(stockOutPage.getTotal());
        
        // 构建映射表
        Map<Long, PmsProductSlimVo> productMap = products.stream()
                .collect(Collectors.toMap(PmsProductSlimVo::getId, t -> t));
        Map<Long, PmsSkuVo> skuMap = skuList.stream()
                .collect(Collectors.toMap(PmsSkuVo::getId, t -> t));
        
        // 组装VO数据
        List<PmsStockOutDetailVo> voList = stockOutPage.getRecords().stream()
                .map(detail -> buildStockOutDetailVo(detail, productMap, skuMap, categoryMap, userMap))
                .collect(Collectors.toList());
        
        page.setRecords(voList);
        return page;
    }

    /**
     * 构建出库明细VO对象
     *
     * @param detail 出库明细实体
     * @param productMap 商品映射
     * @param skuMap SKU映射
     * @param categoryMap 分类映射
     * @param userMap 用户映射
     * @return 出库明细VO
     */
    private PmsStockOutDetailVo buildStockOutDetailVo(
            PmsStockOutDetail detail,
            Map<Long, PmsProductSlimVo> productMap,
            Map<Long, PmsSkuVo> skuMap,
            Map<Long, PmsCategory> categoryMap,
            Map<Long, ExtendSysUserSlimVo> userMap) {
        
        PmsStockOutDetailVo vo = new PmsStockOutDetailVo();
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

        // 设置创建人信息
        if (detail.getCreateId() != null && userMap.containsKey(detail.getCreateId())) {
            ExtendSysUserSlimVo user = userMap.get(detail.getCreateId());
            vo.setCreateName(user.getUsername());
        }
        
        return vo;
    }
}

