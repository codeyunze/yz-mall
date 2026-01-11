package com.yz.mall.pms.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.pms.dto.PmsStockInDetailAddDto;
import com.yz.mall.pms.dto.PmsStockInDetailQueryDto;
import com.yz.mall.pms.dto.PmsStockInDetailUpdateDto;
import com.yz.mall.pms.entity.PmsSku;
import com.yz.mall.pms.entity.PmsStockInDetail;
import com.yz.mall.pms.mapper.PmsStockInDetailMapper;
import com.yz.mall.pms.service.PmsProductQueryService;
import com.yz.mall.pms.service.PmsSkuService;
import com.yz.mall.pms.service.PmsStockInDetailService;
import com.yz.mall.pms.vo.PmsProductSlimVo;
import com.yz.mall.pms.vo.PmsSkuVo;
import com.yz.mall.pms.vo.PmsStockInDetailVo;
import com.yz.mall.serial.service.ExtendSerialService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 产品管理-商品入库日志表(PmsStockInDetail)表服务实现类
 *
 * @author yunze
 * @since 2024-12-25 19:53:27
 */
@Service
public class PmsStockInDetailServiceImpl extends ServiceImpl<PmsStockInDetailMapper, PmsStockInDetail> implements PmsStockInDetailService {

    private final ExtendSerialService extendSerialService;

    private final PmsProductQueryService pmsProductQueryService;

    private final PmsSkuService pmsSkuService;

    public PmsStockInDetailServiceImpl(ExtendSerialService extendSerialService, PmsProductQueryService pmsProductQueryService, PmsSkuService pmsSkuService) {
        this.extendSerialService = extendSerialService;
        this.pmsProductQueryService = pmsProductQueryService;
        this.pmsSkuService = pmsSkuService;
    }

    @Transactional
    @Override
    public Long in(PmsStockInDetailAddDto dto) {
        PmsStockInDetail bo = new PmsStockInDetail();
        bo.setId(IdUtil.getSnowflakeNextId());
        String prefix = "RK" + LocalDateTimeUtil.format(LocalDate.now(), DatePattern.PURE_DATE_PATTERN);
        bo.setStockInCode(extendSerialService.generateNumber(prefix, 6));
        bo.setRemark(dto.getRemark());
        bo.setProductId(dto.getProductId());
        bo.setSkuId(dto.getSkuId());
        bo.setQuantity(dto.getQuantity());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(PmsStockInDetailUpdateDto dto) {
        PmsStockInDetail bo = new PmsStockInDetail();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<PmsStockInDetailVo> page(PageFilter<PmsStockInDetailQueryDto> filter) {
        PmsStockInDetailQueryDto queryFilter = filter.getFilter();
        LambdaQueryWrapper<PmsStockInDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(queryFilter.getStockInCode()), PmsStockInDetail::getStockInCode, queryFilter.getStockInCode());
        queryWrapper.eq(queryFilter.getSkuId() != null && queryFilter.getSkuId() != 0, PmsStockInDetail::getSkuId, queryFilter.getSkuId());
        queryWrapper.orderByDesc(PmsStockInDetail::getId);
        Page<PmsStockInDetail> stockInPage = baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
        if (stockInPage.getTotal() == 0) {
            return new Page<>();
        }

        List<Long> productIds = stockInPage.getRecords().stream()
                .map(PmsStockInDetail::getProductId)
                .filter(id -> id != null && id != 0)
                .distinct()
                .collect(Collectors.toList());
        List<Long> skuIds = stockInPage.getRecords().stream()
                .map(PmsStockInDetail::getSkuId)
                .filter(id -> id != null && id != 0)
                .distinct()
                .collect(Collectors.toList());

        // 查询商品信息
        List<PmsProductSlimVo> products = CollectionUtils.isEmpty(productIds) 
                ? new ArrayList<>() 
                : pmsProductQueryService.getProductByProductIds(productIds);
        
        // 根据skuIds查询SKU的名称和编码信息
        List<PmsSkuVo> skuList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(skuIds)) {
            List<PmsSku> skuEntities = pmsSkuService.listByIds(skuIds);
            if (!CollectionUtils.isEmpty(skuEntities)) {
                skuList = skuEntities.stream().map(sku -> {
                    PmsSkuVo vo = new PmsSkuVo();
                    BeanUtils.copyProperties(sku, vo);
                    return vo;
                }).collect(Collectors.toList());
            }
        }

        if (CollectionUtils.isEmpty(products) && CollectionUtils.isEmpty(skuList)) {
            return new Page<>();
        }

        // 组装商品信息和SKU信息
        Page<PmsStockInDetailVo> page = new Page<>();
        page.setTotal(stockInPage.getTotal());
        Map<Long, PmsProductSlimVo> productMap = products.stream()
                .collect(Collectors.toMap(PmsProductSlimVo::getId, t -> t));
        Map<Long, PmsSkuVo> skuMap = skuList.stream()
                .collect(Collectors.toMap(PmsSkuVo::getId, t -> t));
        
        List<PmsStockInDetailVo> array = new ArrayList<>();
        stockInPage.getRecords().forEach(t -> {
            PmsStockInDetailVo vo = new PmsStockInDetailVo();
            BeanUtils.copyProperties(t, vo);
            
            // 设置商品信息（使用productId）
            if (t.getProductId() != null) {
                PmsProductSlimVo productSlimVo = productMap.get(t.getProductId());
                if (productSlimVo != null) {
                    vo.setProductName(productSlimVo.getProductName());
                    vo.setTitles(productSlimVo.getTitles());
                    vo.setAlbumPics(productSlimVo.getAlbumPics());
                }
            }
            
            // 设置SKU信息（使用skuId）
            if (t.getSkuId() != null) {
                PmsSkuVo skuVo = skuMap.get(t.getSkuId());
                if (skuVo != null) {
                    vo.setSkuCode(skuVo.getSkuCode());
                    vo.setSkuName(skuVo.getSkuName());
                }
            }
            
            array.add(vo);
        });
        page.setRecords(array);
        return page;
    }
}

