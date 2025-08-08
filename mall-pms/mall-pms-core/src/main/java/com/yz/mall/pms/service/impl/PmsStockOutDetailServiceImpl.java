package com.yz.mall.pms.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.dto.PmsStockOutDetailAddDto;
import com.yz.mall.pms.dto.PmsStockOutDetailQueryDto;
import com.yz.mall.pms.dto.PmsStockOutDetailUpdateDto;
import com.yz.mall.pms.entity.PmsStockOutDetail;
import com.yz.mall.pms.mapper.PmsStockOutDetailMapper;
import com.yz.mall.pms.service.PmsProductExpandService;
import com.yz.mall.pms.service.PmsStockOutDetailService;
import com.yz.mall.pms.vo.PmsProductSlimVo;
import com.yz.mall.pms.vo.PmsStockOutDetailVo;
import com.yz.mall.base.PageFilter;
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
 * 产品管理-商品出库日志表(PmsStockOutDetail)表服务实现类
 *
 * @author yunze
 * @since 2024-12-27 12:50:27
 */
@Service
public class PmsStockOutDetailServiceImpl extends ServiceImpl<PmsStockOutDetailMapper, PmsStockOutDetail> implements PmsStockOutDetailService {

    private final ExtendSerialService extendSerialService;

    private final PmsProductExpandService pmsProductExpandService;

    public PmsStockOutDetailServiceImpl(ExtendSerialService extendSerialService, PmsProductExpandService pmsProductExpandService) {
        this.extendSerialService = extendSerialService;
        this.pmsProductExpandService = pmsProductExpandService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long out(PmsStockOutDetailAddDto dto) {
        PmsStockOutDetail bo = new PmsStockOutDetail();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        String prefix = "CK" + LocalDateTimeUtil.format(LocalDate.now(), DatePattern.PURE_DATE_PATTERN);
        bo.setStockOutCode(extendSerialService.generateNumber(prefix, 6));
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean outBatch(List<InternalPmsStockDto> outDetails) {
        String prefix = "CK" + LocalDateTimeUtil.format(LocalDate.now(), DatePattern.PURE_DATE_PATTERN);
        String stockOutCode = extendSerialService.generateNumber(prefix, 6);
        List<PmsStockOutDetail> outDetailList = new ArrayList<>();
        outDetails.forEach(out -> {
            PmsStockOutDetail bo = new PmsStockOutDetail();
            bo.setId(IdUtil.getSnowflakeNextId());
            bo.setStockOutCode(stockOutCode);
            bo.setProductId(out.getProductId());
            bo.setQuantity(out.getQuantity());
            bo.setOrderId(out.getOrderId());
            outDetailList.add(bo);
        });
        return super.saveBatch(outDetailList);
    }

    @Override
    public boolean update(PmsStockOutDetailUpdateDto dto) {
        PmsStockOutDetail bo = new PmsStockOutDetail();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<PmsStockOutDetailVo> page(PageFilter<PmsStockOutDetailQueryDto> filter) {
        PmsStockOutDetailQueryDto queryFilter = filter.getFilter();
        LambdaQueryWrapper<PmsStockOutDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(queryFilter.getStockOutCode()), PmsStockOutDetail::getStockOutCode, queryFilter.getStockOutCode());
        queryWrapper.eq(queryFilter.getProductId() != null && queryFilter.getProductId() != 0, PmsStockOutDetail::getProductId, queryFilter.getProductId());
        queryWrapper.orderByDesc(PmsStockOutDetail::getId);
        Page<PmsStockOutDetail> stockOutPage = baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
        if (stockOutPage.getTotal() == 0) {
            return new Page<>();
        }

        List<Long> productIds = stockOutPage.getRecords().stream().map(PmsStockOutDetail::getProductId).collect(Collectors.toList());
        List<PmsProductSlimVo> products = pmsProductExpandService.getProductByProductIds(productIds);
        if (CollectionUtils.isEmpty(products)) {
            return new Page<>();
        }

        // 组装商品信息
        Page<PmsStockOutDetailVo> page = new Page<>();
        page.setTotal(stockOutPage.getTotal());
        Map<Long, PmsProductSlimVo> productMap = products.stream().collect(Collectors.toMap(PmsProductSlimVo::getId, t -> t));
        List<PmsStockOutDetailVo> array = new ArrayList<>();
        stockOutPage.getRecords().forEach(t -> {
            PmsStockOutDetailVo vo = new PmsStockOutDetailVo();
            BeanUtils.copyProperties(t, vo);
            PmsProductSlimVo productSlimVo = productMap.get(t.getProductId());
            vo.setProductName(productSlimVo.getProductName());
            vo.setTitles(productSlimVo.getTitles());
            vo.setAlbumPics(productSlimVo.getAlbumPics());
            array.add(vo);
        });
        page.setRecords(array);
        return page;
    }
}

