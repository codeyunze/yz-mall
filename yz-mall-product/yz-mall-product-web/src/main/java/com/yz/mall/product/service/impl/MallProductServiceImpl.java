package com.yz.mall.product.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.product.dto.MallProductAddDto;
import com.yz.mall.product.dto.MallProductQueryDto;
import com.yz.mall.product.dto.MallProductUpdateDto;
import com.yz.mall.product.mapper.MallProductMapper;
import com.yz.mall.product.entity.MallProduct;
import com.yz.mall.product.service.MallProductService;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 商品表(MallProduct)表服务实现类
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Service
public class MallProductServiceImpl extends ServiceImpl<MallProductMapper, MallProduct> implements MallProductService {

    @Override
    public String save(MallProductAddDto dto) {
        MallProduct bo = new MallProduct();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextIdStr());
        baseMapper.insert(bo);
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
}

