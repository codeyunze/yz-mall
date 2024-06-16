package com.yz.mall.product.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.product.dto.MallStockAddDto;
import com.yz.mall.product.dto.MallStockQueryDto;
import com.yz.mall.product.dto.MallStockUpdateDto;
import com.yz.mall.product.mapper.MallStockMapper;
import com.yz.mall.product.entity.MallStock;
import com.yz.mall.product.service.MallStockService;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
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
    public String save(MallStockAddDto dto) {
        MallStock bo = new MallStock();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextIdStr());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(MallStockUpdateDto dto) {
        MallStock bo = new MallStock();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<MallStock> page(PageFilter<MallStockQueryDto> filter) {
        LambdaQueryWrapper<MallStock> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @Override
    public Boolean deduct(String productId, Integer quantity) {
        return baseMapper.deduct(productId, quantity);
    }
}

