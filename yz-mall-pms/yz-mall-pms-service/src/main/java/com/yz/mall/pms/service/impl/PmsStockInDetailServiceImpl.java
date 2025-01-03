package com.yz.mall.pms.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.pms.vo.PmsProductSlimVo;
import com.yz.mall.pms.dto.PmsStockInDetailAddDto;
import com.yz.mall.pms.dto.PmsStockInDetailQueryDto;
import com.yz.mall.pms.dto.PmsStockInDetailUpdateDto;
import com.yz.mall.pms.entity.PmsStockInDetail;
import com.yz.mall.pms.mapper.PmsStockInDetailMapper;
import com.yz.mall.pms.service.PmsProductExpandService;
import com.yz.mall.pms.service.PmsStockInDetailService;
import com.yz.mall.pms.vo.PmsStockInDetailVo;
import com.yz.mall.web.common.PageFilter;
import com.yz.unqid.service.InternalUnqidService;
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

    private final InternalUnqidService internalUnqidService;

    private final PmsProductExpandService pmsProductExpandService;

    public PmsStockInDetailServiceImpl(InternalUnqidService internalUnqidService, PmsProductExpandService pmsProductExpandService) {
        this.internalUnqidService = internalUnqidService;
        this.pmsProductExpandService = pmsProductExpandService;
    }

    @Transactional
    @Override
    public Long in(PmsStockInDetailAddDto dto) {
        PmsStockInDetail bo = new PmsStockInDetail();
        bo.setId(IdUtil.getSnowflakeNextId());
        String prefix = "RK" + LocalDateTimeUtil.format(LocalDate.now(), DatePattern.PURE_DATE_PATTERN);
        bo.setStockInCode(internalUnqidService.generateSerialNumber(prefix, 6));
        bo.setRemark(dto.getRemark());
        bo.setProductId(dto.getProductId());
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
        queryWrapper.eq(queryFilter.getProductId() != null && queryFilter.getProductId() != 0, PmsStockInDetail::getProductId, queryFilter.getProductId());
        queryWrapper.orderByDesc(PmsStockInDetail::getId);
        Page<PmsStockInDetail> stockInPage = baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
        if (stockInPage.getTotal() == 0) {
            return new Page<>();
        }

        List<Long> productIds = stockInPage.getRecords().stream().map(PmsStockInDetail::getProductId).collect(Collectors.toList());
        List<PmsProductSlimVo> products = pmsProductExpandService.getProductByProductIds(productIds);
        if (CollectionUtils.isEmpty(products)) {
            return new Page<>();
        }

        // 组装商品信息
        Page<PmsStockInDetailVo> page = new Page<>();
        page.setTotal(stockInPage.getTotal());
        Map<Long, PmsProductSlimVo> productMap = products.stream().collect(Collectors.toMap(PmsProductSlimVo::getId, t -> t));
        List<PmsStockInDetailVo> array = new ArrayList<>();
        stockInPage.getRecords().forEach(t -> {
            PmsStockInDetailVo vo = new PmsStockInDetailVo();
            BeanUtils.copyProperties(t, vo);
            PmsProductSlimVo productSlimVo = productMap.get(t.getProductId());
            vo.setProductName(productSlimVo.getName());
            vo.setTitles(productSlimVo.getTitles());
            vo.setAlbumPics(productSlimVo.getAlbumPics());
            array.add(vo);
        });
        page.setRecords(array);
        return page;
    }
}

