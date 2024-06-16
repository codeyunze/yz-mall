package com.yz.mall.product.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.product.dto.MallStockQueryDto;
import com.yz.mall.product.entity.MallStock;
import com.yz.mall.product.mapper.MallStockMapper;
import com.yz.mall.product.service.MallStockService;
import com.yz.tools.PageFilter;
import org.springframework.stereotype.Service;

/**
 * 商品库存表(MallStock)表服务实现类
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
@Service
public class MallStockServiceImpl extends ServiceImpl<MallStockMapper, MallStock> implements MallStockService {

    @Override
    public Page<MallStock> page(PageFilter<MallStockQueryDto> filter) {
        LambdaQueryWrapper<MallStock> queryWrapper = new LambdaQueryWrapper<>();
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
        MallStock stock = baseMapper.selectOne(new LambdaQueryWrapper<MallStock>().select(MallStock::getQuantity).eq(MallStock::getProductId, productId));
        if (stock == null) {
            stock = new MallStock();
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

