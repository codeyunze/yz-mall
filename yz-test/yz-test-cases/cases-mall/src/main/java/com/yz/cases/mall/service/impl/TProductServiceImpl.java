package com.yz.cases.mall.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.cases.mall.dto.TProductAddDto;
import com.yz.cases.mall.dto.TProductQueryDto;
import com.yz.cases.mall.dto.TProductUpdateDto;
import com.yz.cases.mall.entity.TProduct;
import com.yz.cases.mall.mapper.TProductMapper;
import com.yz.cases.mall.service.TProductService;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商品信息(TProduct)表服务实现类
 *
 * @author yunze
 * @since 2024-06-13 08:38:51
 */

@Service
public class TProductServiceImpl extends ServiceImpl<TProductMapper, TProduct> implements TProductService {

    @DS("slave")
    @Override
    public Long save(TProductAddDto dto) {
        TProduct bo = new TProduct();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(TProductUpdateDto dto) {
        TProduct bo = new TProduct();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @DS("#session.rw")
    @Override
    public Page<TProduct> page(PageFilter<TProductQueryDto> filter) {
        LambdaQueryWrapper<TProduct> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @Override
    public void createTable() {
        // 昨天
        String yesterday = DateUtil.format(DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, -1), DatePattern.PURE_DATE_PATTERN);
        baseMapper.createTable("t_product_" + yesterday);
    }

    @Override
    public void generateData() {
        // 高价值商品
        List<String> highProducs = new ArrayList<>();
        highProducs.add("比亚迪");
        highProducs.add("长安UNI-V");
        highProducs.add("奔驰C300");
        // 中价值商品
        List<String> mediumProducs = new ArrayList<>();
        mediumProducs.add("小米");
        mediumProducs.add("华为");
        mediumProducs.add("荣耀");
        mediumProducs.add("苹果");
        mediumProducs.add("大疆");
        // 低价值商品
        List<String> lowProducs = new ArrayList<>();
        lowProducs.add("香蕉");
        lowProducs.add("菠萝");
        lowProducs.add("海南椰子");

        List<TProduct> products = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            TProduct product = new TProduct();
            product.setId(IdUtil.getSnowflakeNextId());
            product.setName(highProducs.get(i % highProducs.size()) + "-" + i);
            product.setPrice(RandomUtil.randomBigDecimal(BigDecimal.valueOf(100000), BigDecimal.valueOf(10000000)).setScale(2, RoundingMode.HALF_UP));
            products.add(product);
        }

        for (int i = 0; i < 50; i++) {
            TProduct product = new TProduct();
            product.setId(IdUtil.getSnowflakeNextId());
            product.setName(mediumProducs.get(i % mediumProducs.size()) + "-" + i);
            product.setPrice(RandomUtil.randomBigDecimal(BigDecimal.valueOf(1000), BigDecimal.valueOf(15000)).setScale(2, RoundingMode.HALF_UP));
            products.add(product);
        }

        for (int i = 0; i < 50; i++) {
            TProduct product = new TProduct();
            product.setId(IdUtil.getSnowflakeNextId());
            product.setName(lowProducs.get(i % lowProducs.size()) + "-" + i);
            product.setPrice(RandomUtil.randomBigDecimal(BigDecimal.valueOf(5), BigDecimal.valueOf(50)).setScale(2, RoundingMode.HALF_UP));
            products.add(product);
        }

        super.saveBatch(products);
    }
}

