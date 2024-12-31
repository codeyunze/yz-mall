package com.yz.dynamic.datasource.one.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.dynamic.datasource.one.annotation.DS;
import com.yz.dynamic.datasource.one.dto.ProductAddDto;
import com.yz.dynamic.datasource.one.entity.TProduct;
import com.yz.dynamic.datasource.one.mapper.TProductMapper;
import com.yz.dynamic.datasource.one.service.TProductService;
import com.yz.mall.web.common.enums.DataSourceTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 商品信息(TProduct)表服务实现类
 *
 * @author yunze
 * @since 2023-10-29 18:01:13
 */
@Slf4j
@Service("tProductService")
public class TProductServiceImpl extends ServiceImpl<TProductMapper, TProduct> implements TProductService {

    @DS(DataSourceTypeEnum.product)
    @Override
    public Integer save(ProductAddDto dto) {
        dto.setId(IdUtil.getSnowflake().nextId());
        return baseMapper.save(dto);
    }

    // @DS(DataSourceTypeEnum.stock)
    @Override
    public List<TProduct> customList() {
        LambdaQueryWrapper<TProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TProduct::getInvalid, 0);
        return baseMapper.selectList(queryWrapper);
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

