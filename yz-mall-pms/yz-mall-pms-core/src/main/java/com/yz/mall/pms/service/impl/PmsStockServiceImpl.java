package com.yz.mall.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.pms.dto.PmsStockQueryDto;
import com.yz.mall.pms.entity.PmsStock;
import com.yz.mall.pms.mapper.PmsStockMapper;
import com.yz.mall.pms.service.PmsStockService;
import com.yz.tools.PageFilter;
import org.springframework.stereotype.Service;

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

