package com.yz.mall.product.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.product.dto.MallProductAddDto;
import com.yz.mall.product.dto.MallProductQueryDto;
import com.yz.mall.product.dto.MallProductUpdateDto;
import com.yz.mall.product.mapper.MallProductMapper;
import com.yz.mall.product.entity.MallProduct;
import com.yz.mall.product.service.MallProductService;
import com.yz.mall.product.service.MallStockService;
import com.yz.mall.product.vo.MallProductVo;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 商品表(MallProduct)表服务实现类
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Service
public class MallProductServiceImpl extends ServiceImpl<MallProductMapper, MallProduct> implements MallProductService {

    private final MallStockService stockService;

    public MallProductServiceImpl(MallStockService stockService) {
        this.stockService = stockService;
    }

    @Transactional
    @Override
    public String save(MallProductAddDto dto) {
        MallProduct bo = new MallProduct();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextIdStr());
        baseMapper.insert(bo);

        // 添加商品库存信息
        stockService.add(bo.getId(), 0);
        return bo.getId();
    }

    @Override
    public boolean update(MallProductUpdateDto dto) {
        MallProduct bo = new MallProduct();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<MallProduct> page(PageFilter<MallProductQueryDto> filter) {
        LambdaQueryWrapper<MallProduct> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @DS("slave")
    @Override
    public MallProductVo detail(String id) {
        MallProduct bo = baseMapper.selectById(id);
        MallProductVo product = new MallProductVo();
        BeanUtils.copyProperties(bo, product);

        product.setQuantity(stockService.getStockByProductId(id));
        return product;
    }
}