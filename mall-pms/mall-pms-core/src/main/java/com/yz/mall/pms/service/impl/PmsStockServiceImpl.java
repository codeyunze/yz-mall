package com.yz.mall.pms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
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
import com.yz.mall.pms.entity.PmsStockLog;
import com.yz.mall.pms.mapper.PmsStockLogMapper;
import com.yz.mall.pms.mapper.PmsStockMapper;
import com.yz.mall.pms.entity.PmsSku;
import com.yz.mall.pms.service.PmsSkuService;
import com.yz.mall.pms.service.PmsStockInDetailService;
import com.yz.mall.pms.service.PmsStockOutDetailService;
import com.yz.mall.pms.service.PmsStockService;
import com.yz.mall.pms.vo.PmsSkuVo;
import com.yz.mall.pms.vo.PmsProductStockVo;
import com.yz.mall.pms.vo.PmsSkuStockVo;
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

    private final PmsStockLogMapper pmsStockLogMapper;

    public PmsStockServiceImpl(PmsStockOutDetailService pmsStockOutDetailService
            , PmsStockInDetailService pmsStockInDetailService
            , PmsSkuService skuService
            , PmsStockLogMapper pmsStockLogMapper) {
        this.pmsStockOutDetailService = pmsStockOutDetailService;
        this.pmsStockInDetailService = pmsStockInDetailService;
        this.skuService = skuService;
        this.pmsStockLogMapper = pmsStockLogMapper;
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
        resolveSkuIdIfNeeded(deductStock);
        // TODO: 2024/6/16 星期日 yunze 加锁
        LambdaQueryWrapper<PmsStock> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PmsStock::getSkuId, deductStock.getSkuId());
        queryWrapper.ge(PmsStock::getQuantity, deductStock.getQuantity());
        PmsStock stock = baseMapper.selectOne(queryWrapper);
        if (stock == null) {
            throw new BusinessException("SKU" + deductStock.getSkuId() + "库存不足");
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
        if (CollectionUtils.isEmpty(productStocks)) {
            return;
        }
        // 兼容调用方误传 productId 作为 skuId：先解析为真实 SKU
        for (ExtendPmsStockDto productStock : productStocks) {
            resolveSkuIdIfNeeded(productStock);
        }

        // 各SKU需要扣除的库存数量
        Map<Long, Integer> skuQuantityMap = productStocks.stream().collect(Collectors.toMap(ExtendPmsStockDto::getSkuId, ExtendPmsStockDto::getQuantity, Integer::sum));
        List<Long> skuIds = new ArrayList<>(skuQuantityMap.keySet());
        // TODO: 2024/6/27 星期四 yunze 锁对应SKU的库存
        List<PmsStock> stocks = getPmsStocksBySkuIds(skuIds);
        Map<Long, PmsStock> stockBySkuIdMap = stocks.stream().collect(Collectors.toMap(PmsStock::getSkuId, t -> t, (a, b) -> a));

        for (Long skuId : skuIds) {
            // 指定SKU的库存信息
            PmsStock stock = stockBySkuIdMap.get(skuId);
            Integer needQuantity = skuQuantityMap.get(skuId);
            if (stock == null || stock.getQuantity() < needQuantity) {
                log.info("SKU{}库存不足", skuId);
                throw new BusinessException("SKU" + skuId + "库存不足");
            }
            int beforeQty = stock.getQuantity() == null ? 0 : stock.getQuantity();
            stock.setQuantity(beforeQty - needQuantity);
            saveStockLog(stock, 2, needQuantity, beforeQty, stock.getQuantity(),
                    productStocks.stream().filter(p -> skuId.equals(p.getSkuId())).findFirst().orElse(null));
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

    /**
     * 记录库存变更流水
     */
    private void saveStockLog(PmsStock stock, int changeType, int changeQty, int beforeQty, int afterQty, ExtendPmsStockDto biz) {
        PmsStockLog logEntity = new PmsStockLog();
        logEntity.setId(IdUtil.getSnowflakeNextId());
        logEntity.setSkuId(stock.getSkuId());
        logEntity.setProductId(stock.getProductId());
        logEntity.setWarehouseId(stock.getWarehouseId() == null ? 0L : stock.getWarehouseId());
        logEntity.setChangeType(changeType);
        logEntity.setChangeQty(changeQty);
        logEntity.setBeforeQty(beforeQty);
        logEntity.setAfterQty(afterQty);
        if (biz != null) {
            logEntity.setOrderId(biz.getOrderId());
            logEntity.setRemark(biz.getRemark());
        }
        pmsStockLogMapper.insert(logEntity);
    }

    /**
     * 将扣减请求中的 skuId 解析为真实库存 SKU。
     * <p>
     * 兼容历史调用：仅传 productId，或把 productId 误当作 skuId。
     * 多规格时优先选用库存充足的第一个 SKU。
     */
    private void resolveSkuIdIfNeeded(ExtendPmsStockDto deductStock) {
        if (deductStock == null) {
            throw new BusinessException("扣减库存参数不能为空");
        }
        Long skuId = deductStock.getSkuId();
        Long productId = deductStock.getProductId();
        Integer needQuantity = deductStock.getQuantity() == null ? 0 : deductStock.getQuantity();

        if (skuId != null) {
            Integer stockQty = baseMapper.getStockBySkuId(skuId);
            if (stockQty != null) {
                if (productId == null) {
                    PmsSku sku = skuService.getById(skuId);
                    if (sku != null) {
                        deductStock.setProductId(sku.getProductId());
                    }
                }
                return;
            }
        }

        Long lookupProductId = productId != null ? productId : skuId;
        if (lookupProductId == null) {
            throw new BusinessException("商品SKU不能为空");
        }

        List<PmsSkuVo> skuList = skuService.listByProductId(lookupProductId);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new BusinessException("商品未配置SKU，无法扣减库存");
        }

        List<Long> candidateSkuIds = skuList.stream().map(PmsSkuVo::getId).collect(Collectors.toList());
        Map<Long, Integer> stockMap = getStockBySkuIds(candidateSkuIds);
        Long resolvedSkuId = null;
        if (skuList.size() == 1) {
            resolvedSkuId = skuList.get(0).getId();
        } else {
            for (PmsSkuVo sku : skuList) {
                if (stockMap.getOrDefault(sku.getId(), 0) >= needQuantity) {
                    resolvedSkuId = sku.getId();
                    break;
                }
            }
            if (resolvedSkuId == null) {
                resolvedSkuId = skuList.get(0).getId();
            }
            log.warn("商品{}存在多规格且未指定SKU，自动选用SKU{}", lookupProductId, resolvedSkuId);
        }

        if (stockMap.getOrDefault(resolvedSkuId, 0) < needQuantity) {
            throw new BusinessException("SKU" + resolvedSkuId + "库存不足");
        }
        deductStock.setSkuId(resolvedSkuId);
        deductStock.setProductId(lookupProductId);
    }

    private List<PmsStock> getPmsStocksBySkuIds(List<Long> skuIds) {
        LambdaQueryWrapper<PmsStock> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PmsStock::getId, PmsStock::getSkuId, PmsStock::getQuantity);
        queryWrapper.in(PmsStock::getSkuId, skuIds);
        // 查询指定SKU的库存数量
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 增加库存时解析真实 skuId：若传入 skuId 并非有效 SKU，则按 productId 取第一个规格。
     */
    private void resolveSkuIdForAddIfNeeded(ExtendPmsStockDto addStock) {
        if (addStock == null || addStock.getSkuId() == null) {
            throw new BusinessException("商品SKU不能为空");
        }
        PmsSku sku = skuService.getById(addStock.getSkuId());
        if (sku != null) {
            if (addStock.getProductId() == null) {
                addStock.setProductId(sku.getProductId());
            }
            return;
        }
        Long lookupProductId = addStock.getProductId() != null ? addStock.getProductId() : addStock.getSkuId();
        List<PmsSkuVo> skuList = skuService.listByProductId(lookupProductId);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new BusinessException("商品未配置SKU，无法增加库存");
        }
        addStock.setSkuId(skuList.get(0).getId());
        addStock.setProductId(lookupProductId);
        if (skuList.size() > 1) {
            log.warn("商品{}存在多规格且未指定SKU，回补库存自动选用SKU{}", lookupProductId, addStock.getSkuId());
        }
    }

    @Transactional
    @Override
    public Boolean add(ExtendPmsStockDto addStock) {
        // TODO: 2024/6/16 星期日 yunze 加锁
        // 订单退款等场景可能只传 productId（skuId 暂用 productId），需解析真实 SKU
        resolveSkuIdForAddIfNeeded(addStock);
        PmsStock stock = baseMapper.selectOne(new LambdaQueryWrapper<PmsStock>().select(PmsStock::getId, PmsStock::getSkuId, PmsStock::getProductId, PmsStock::getWarehouseId, PmsStock::getQuantity).eq(PmsStock::getSkuId, addStock.getSkuId()));
        int beforeQty = 0;
        if (stock == null || stock.getId() == null) {
            stock = new PmsStock();
            stock.setSkuId(addStock.getSkuId());
            stock.setProductId(addStock.getProductId());
            stock.setWarehouseId(0L);
            stock.setQuantity(addStock.getQuantity());
            stock.setCreateId(StpUtil.getLoginIdAsLong());
        } else {
            beforeQty = stock.getQuantity() == null ? 0 : stock.getQuantity();
            stock.setQuantity(beforeQty + addStock.getQuantity());
            stock.setUpdateId(StpUtil.getLoginIdAsLong());
        }
        if (!super.saveOrUpdate(stock)) {
            return false;
        }
        saveStockLog(stock, 5, addStock.getQuantity(), beforeQty, stock.getQuantity(), addStock);

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
    public List<PmsSkuStockVo> listSkuStockByProductId(Long productId) {
        if (productId == null) {
            return new ArrayList<>();
        }
        List<PmsSkuVo> skuList = skuService.listByProductId(productId);
        if (CollectionUtils.isEmpty(skuList)) {
            return new ArrayList<>();
        }
        List<Long> skuIds = skuList.stream().map(PmsSkuVo::getId).collect(Collectors.toList());
        Map<Long, Integer> stockMap = getStockBySkuIds(skuIds);
        List<PmsSkuStockVo> result = new ArrayList<>(skuList.size());
        for (PmsSkuVo sku : skuList) {
            PmsSkuStockVo vo = new PmsSkuStockVo();
            vo.setSkuId(sku.getId());
            vo.setProductId(productId);
            vo.setSkuCode(sku.getSkuCode());
            vo.setSkuName(sku.getSkuName());
            vo.setQuantity(stockMap.getOrDefault(sku.getId(), 0));
            result.add(vo);
        }
        return result;
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

