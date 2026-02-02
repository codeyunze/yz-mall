package com.yz.mall.pms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.pms.dto.ExtendPmsStockDto;
import com.yz.mall.pms.dto.PmsStockInDetailAddDto;
import com.yz.mall.pms.dto.PmsStockOutDetailAddDto;
import com.yz.mall.pms.dto.PmsStockQueryDto;
import com.yz.mall.pms.entity.PmsStock;
import com.yz.mall.pms.mapper.PmsStockMapper;
import com.yz.mall.pms.entity.PmsSku;
import com.yz.mall.pms.service.PmsSkuService;
import com.yz.mall.pms.service.PmsStockInDetailService;
import com.yz.mall.pms.service.PmsStockOutDetailService;
import com.yz.mall.pms.service.PmsStockService;
import com.yz.mall.pms.vo.PmsSkuVo;
import com.yz.mall.pms.vo.ExtendPmsStockDeductVo;
import com.yz.mall.pms.vo.PmsProductStockVo;
import com.yz.mall.pms.vo.PmsStockVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品库存表(PmsStock)表服务实现类
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
@Slf4j
@Service
public class PmsStockServiceImpl extends ServiceImpl<PmsStockMapper, PmsStock> implements PmsStockService {

    private final PmsStockOutDetailService pmsStockOutDetailService;

    private final PmsStockInDetailService pmsStockInDetailService;

    private final PmsSkuService skuService;

    public PmsStockServiceImpl(PmsStockOutDetailService pmsStockOutDetailService
            , PmsStockInDetailService pmsStockInDetailService
            , PmsSkuService skuService) {
        this.pmsStockOutDetailService = pmsStockOutDetailService;
        this.pmsStockInDetailService = pmsStockInDetailService;
        this.skuService = skuService;
    }

    @DS("slave")
    @Override
    public Page<PmsProductStockVo> page(PageFilter<PmsStockQueryDto> filter) {
        Page<PmsProductStockVo> page = baseMapper.selectPageByFilter(new Page<>(filter.getCurrent(), filter.getSize()), filter.getFilter());
        if (page.getTotal() == 0) {
            return page;
        }

        List<Long> productIds = page.getRecords().stream().map(PmsProductStockVo::getProductId).collect(Collectors.toList());
        // 根据 productId 查询对应的 SKU 列表，然后查询库存并汇总
        Map<Long, Integer> productStockMap = new HashMap<>();
        for (Long productId : productIds) {
            List<PmsSkuVo> skuList = skuService.listByProductId(productId);
            if (!CollectionUtils.isEmpty(skuList)) {
                List<Long> skuIds = skuList.stream().map(PmsSkuVo::getId).collect(Collectors.toList());
                List<PmsStockVo> stocks = baseMapper.selectStockBySkuIds(skuIds);
                if (!CollectionUtils.isEmpty(stocks)) {
                    int totalStock = stocks.stream().mapToInt(PmsStockVo::getQuantity).sum();
                    productStockMap.put(productId, totalStock);
                } else {
                    productStockMap.put(productId, 0);
                }
            } else {
                productStockMap.put(productId, 0);
            }
        }
        
        page.getRecords().forEach(product -> {
            product.setQuantity(productStockMap.getOrDefault(product.getProductId(), 0));
        });
        return page;
    }

    // TODO: 2024/6/16 星期日 yunze 加事务
    @DS("master")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deduct(ExtendPmsStockDto deductStock) {
        // TODO: 2024/6/16 星期日 yunze 加锁
        LambdaQueryWrapper<PmsStock> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PmsStock::getSkuId, deductStock.getSkuId());
        queryWrapper.ge(PmsStock::getQuantity, deductStock.getQuantity());
        PmsStock stock = baseMapper.selectOne(queryWrapper);
        if (stock == null) {
            throw new BusinessException("SKU库存不足");
        }

        boolean deducted = baseMapper.deduct(deductStock.getSkuId(), deductStock.getQuantity());
        if (!deducted) {
            return false;
        }

        // 根据 skuId 获取对应的 productId
        Long productId = deductStock.getProductId();
        if (productId == null) {
            PmsSku sku = skuService.getById(deductStock.getSkuId());
            if (sku != null) {
                productId = sku.getProductId();
            }
        }
        Long saved = pmsStockOutDetailService.out(new PmsStockOutDetailAddDto(productId, deductStock.getSkuId(), deductStock.getQuantity(), deductStock.getRemark()));
        return saved != null;
    }

    @DS("master")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deduct(List<ExtendPmsStockDto> productStocks) {
        // 各SKU需要扣除的库存数量
        Map<Long, Integer> skuQuantityMap = productStocks.stream().collect(Collectors.toMap(ExtendPmsStockDto::getSkuId, ExtendPmsStockDto::getQuantity));
        List<Long> skuIds = productStocks.stream().map(ExtendPmsStockDto::getSkuId).collect(Collectors.toList());
        // TODO: 2024/6/27 星期四 yunze 锁对应SKU的库存
        List<PmsStock> stocks = getPmsStocksBySkuIds(skuIds);
        Map<Long, PmsStock> stockBySkuIdMap = stocks.stream().collect(Collectors.toMap(PmsStock::getSkuId, t -> t));

        // 扣减库存结果
        List<ExtendPmsStockDeductVo> deductStocks = new ArrayList<>();
        for (Long skuId : skuIds) {
            ExtendPmsStockDeductVo deductVo = new ExtendPmsStockDeductVo();
            deductVo.setSkuId(skuId);
            // 指定SKU的库存信息
            PmsStock stock = stockBySkuIdMap.get(skuId);
            if (stock == null || stock.getQuantity() < skuQuantityMap.get(skuId)) {
                log.info("SKU{}库存不足", skuId);
                throw new BusinessException("SKU" + skuId + "库存不足");
            } else {
                stock.setQuantity(stock.getQuantity() - skuQuantityMap.get(skuId));
            }
        }

        if (CollectionUtils.isEmpty(stocks)) {
            // 表示没有选择的SKU的库存信息
            try {
                throw new BusinessException("SKU" + JacksonUtil.getObjectMapper().writeValueAsString(skuIds) + "库存不足");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        // TODO: 2025/2/1 yunze 库存扣减方式需要优化
        if (!super.updateBatchById(stocks)) {
            throw new BusinessException("商品库存扣除失败");
        }

        // 记录出库明细
        if (!pmsStockOutDetailService.outBatch(productStocks)) {
            throw new BusinessException("商品库存扣除失败");
        }
    }

    private List<PmsStock> getPmsStocksBySkuIds(List<Long> skuIds) {
        LambdaQueryWrapper<PmsStock> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PmsStock::getId, PmsStock::getSkuId, PmsStock::getQuantity);
        queryWrapper.in(PmsStock::getSkuId, skuIds);
        // 查询指定SKU的库存数量
        return baseMapper.selectList(queryWrapper);
    }

    @Transactional
    @Override
    public Boolean add(ExtendPmsStockDto addStock) {
        // TODO: 2024/6/16 星期日 yunze 加锁
        PmsStock stock = baseMapper.selectOne(new LambdaQueryWrapper<PmsStock>().select(PmsStock::getId, PmsStock::getQuantity).eq(PmsStock::getSkuId, addStock.getSkuId()));
        if (stock == null || stock.getId() == null) {
            stock = new PmsStock();
            stock.setSkuId(addStock.getSkuId());
            stock.setQuantity(addStock.getQuantity());
            stock.setCreateId(StpUtil.getLoginIdAsLong());
        } else {
            stock.setQuantity(stock.getQuantity() + addStock.getQuantity());
            stock.setUpdateId(StpUtil.getLoginIdAsLong());
        }
        if (!super.saveOrUpdate(stock)) {
            return false;
        }

        // 根据 skuId 获取对应的 productId
        Long productId = addStock.getProductId();
        if (productId == null) {
            PmsSku sku = skuService.getById(addStock.getSkuId());
            if (sku != null) {
                productId = sku.getProductId();
            }
        }
        // 同时设置 productId 到 stock 中
        if (productId != null && stock.getProductId() == null) {
            stock.setProductId(productId);
            super.updateById(stock);
        }
        Long saved = pmsStockInDetailService.in(new PmsStockInDetailAddDto(productId, addStock.getSkuId(), addStock.getQuantity(), addStock.getRemark()));
        return saved != null;
    }

    @Override
    public Integer getStockBySkuId(Long skuId) {
        return baseMapper.getStockBySkuId(skuId);
    }

    @DS("slave")
    @Override
    public Map<Long, Integer> getStockBySkuIds(List<Long> skuIds) {
        LambdaQueryWrapper<PmsStock> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PmsStock::getSkuId, PmsStock::getQuantity);
        queryWrapper.in(PmsStock::getSkuId, skuIds);
        List<PmsStock> stocks = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(stocks)) {
            Map<Long, Integer> map = new HashMap<>();
            skuIds.forEach(skuId -> {
                map.put(skuId, 0);
            });
            return map;
        }
        return stocks.stream().collect(Collectors.toMap(PmsStock::getSkuId, t -> t.getQuantity() == null ? 0 : t.getQuantity()));
    }
}

