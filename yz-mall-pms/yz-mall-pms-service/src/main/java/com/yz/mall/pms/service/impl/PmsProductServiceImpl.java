package com.yz.mall.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.pms.entity.PmsProduct;
import com.yz.mall.pms.mapper.PmsProductMapper;
import com.yz.mall.pms.service.PmsProductService;
import com.yz.mall.pms.dto.PmsProductAddDto;
import com.yz.mall.pms.dto.PmsProductQueryDto;
import com.yz.mall.pms.dto.PmsProductUpdateDto;
import com.yz.mall.pms.service.PmsStockService;
import com.yz.mall.pms.vo.PmsProductVo;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 商品表(PmsProduct)表服务实现类
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Service
public class PmsProductServiceImpl extends ServiceImpl<PmsProductMapper, PmsProduct> implements PmsProductService {

    private final PmsStockService stockService;

    public PmsProductServiceImpl(PmsStockService stockService) {
        this.stockService = stockService;
    }

    @Transactional
    @Override
    public Long save(PmsProductAddDto dto) {
        PmsProduct bo = new PmsProduct();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);

        // 添加商品库存信息
        stockService.add(bo.getId(), 0);
        return bo.getId();
    }

    @Override
    public boolean update(PmsProductUpdateDto dto) {
        PmsProduct bo = new PmsProduct();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<PmsProduct> page(PageFilter<PmsProductQueryDto> filter) {
        PmsProductQueryDto query = filter.getFilter();
        LambdaQueryWrapper<PmsProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CollectionUtils.isEmpty(query.getTitles()), PmsProduct::getTitle, query.getTitles());
        queryWrapper.eq(query.getVerifyStatus() != null, PmsProduct::getVerifyStatus, query.getVerifyStatus());
        queryWrapper.eq(query.getPublishStatus() != null, PmsProduct::getPublishStatus, query.getPublishStatus());
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @DS("slave")
    @Override
    public PmsProductVo detail(Long id) {
        PmsProduct bo = baseMapper.selectById(id);
        PmsProductVo product = new PmsProductVo();
        BeanUtils.copyProperties(bo, product);

        product.setQuantity(stockService.getStockByProductId(id));
        return product;
    }
}