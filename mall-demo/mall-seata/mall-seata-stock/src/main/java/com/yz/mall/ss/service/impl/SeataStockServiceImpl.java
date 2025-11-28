package com.yz.mall.ss.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.ss.dto.SeataStockAddDto;
import com.yz.mall.ss.dto.SeataStockQueryDto;
import com.yz.mall.ss.dto.SeataStockUpdateDto;
import com.yz.mall.ss.mapper.SeataStockMapper;
import com.yz.mall.ss.entity.SeataStock;
import com.yz.mall.ss.service.SeataStockService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 分布式事务-库存表(SeataStock)表服务实现类
 *
 * @author yunze
 * @since 2025-11-26 15:29:48
 */
@Service
public class SeataStockServiceImpl extends ServiceImpl<SeataStockMapper, SeataStock> implements SeataStockService {

    @Override
    public Long save(SeataStockAddDto dto) {
        SeataStock bo = new SeataStock();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SeataStockUpdateDto dto) {
        SeataStock bo = new SeataStock();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SeataStock> page(PageFilter<SeataStockQueryDto> filter) {
        LambdaQueryWrapper<SeataStock> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @Override
    public boolean decreaseStock(Long productId, Integer productStock) {
        LambdaQueryWrapper<SeataStock> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeataStock::getProductId, productId);
        SeataStock stock = baseMapper.selectOne(queryWrapper);
        if (stock == null) {
            throw new BusinessException("商品不存在库存");
        }

        if (stock.getProductStock() < productStock) {
            throw new BusinessException("商品库存不足");
        }

        LambdaUpdateWrapper<SeataStock> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SeataStock::getProductId, productId)
                .apply("product_stock >= {0}", productStock)
                .set(SeataStock::getProductStock, stock.getProductStock() - productStock);

        int updated = baseMapper.update(updateWrapper);
        if (updated == 0) {
            throw new BusinessException("商品库存不足，扣减失败");
        }
        return true;
    }
}

