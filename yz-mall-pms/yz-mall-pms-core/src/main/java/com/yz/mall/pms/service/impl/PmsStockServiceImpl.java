package com.yz.mall.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.advice.exception.BusinessException;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.dto.PmsStockQueryDto;
import com.yz.mall.pms.entity.PmsStock;
import com.yz.mall.pms.mapper.PmsStockMapper;
import com.yz.mall.pms.service.PmsStockService;
import com.yz.tools.PageFilter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品库存表(PmsStock)表服务实现类
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
@Service
public class PmsStockServiceImpl extends ServiceImpl<PmsStockMapper, PmsStock> implements PmsStockService {

    @Override
    public Page<PmsStock> page(PageFilter<PmsStockQueryDto> filter) {
        LambdaQueryWrapper<PmsStock> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    // TODO: 2024/6/16 星期日 yunze 加事务
    @Override
    public Boolean deduct(String productId, Integer quantity) {
        // TODO: 2024/6/16 星期日 yunze 加锁
        return baseMapper.deduct(productId, quantity);
    }

    @Override
    public void deduct(List<InternalPmsStockDto> productStocks) {
        // 各商品需要扣除的库存数量
        Map<String, Integer> productQuantityuMap = productStocks.stream().collect(Collectors.toMap(InternalPmsStockDto::getProductId, InternalPmsStockDto::getQuantity));
        List<String> productIds = productStocks.stream().map(InternalPmsStockDto::getProductId).collect(Collectors.toList());
        // TODO: 2024/6/27 星期四 yunze 锁对应商品的库存
        LambdaQueryWrapper<PmsStock> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PmsStock::getId, PmsStock::getProductId, PmsStock::getQuantity);
        queryWrapper.in(PmsStock::getProductId, productIds);
        // 查询指定商品的库存数量
        List<PmsStock> stocks = baseMapper.selectList(queryWrapper);
        for (int i = 0; i < stocks.size(); i++) {
            PmsStock stock = stocks.get(i);
            if (stock.getQuantity() < productQuantityuMap.get(stock.getProductId())) {
                throw new BusinessException("商品" + stock.getProductId() + "库存不足");
            } else {
                stocks.get(i).setQuantity(stock.getQuantity() - productQuantityuMap.get(stock.getProductId()));
            }
        }
        super.updateBatchById(stocks);
    }

    // TODO: 2024/6/16 星期日 yunze 加事务
    @Override
    public Boolean add(String productId, Integer quantity) {
        // TODO: 2024/6/16 星期日 yunze 加锁
        PmsStock stock = baseMapper.selectOne(new LambdaQueryWrapper<PmsStock>().select(PmsStock::getQuantity).eq(PmsStock::getProductId, productId));
        if (stock == null) {
            stock = new PmsStock();
            stock.setId(IdUtil.getSnowflakeNextIdStr());
            stock.setProductId(productId);
            stock.setQuantity(quantity);
        } else {
            stock.setQuantity(stock.getQuantity() + quantity);
        }
        return super.saveOrUpdate(stock);
    }

    @Override
    public Integer getStockByProductId(String productId) {
        return baseMapper.getStockByProductId(productId);
    }
}

