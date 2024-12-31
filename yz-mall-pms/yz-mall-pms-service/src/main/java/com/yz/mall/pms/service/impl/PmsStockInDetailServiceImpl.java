package com.yz.mall.pms.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.pms.dto.PmsStockInDetailAddDto;
import com.yz.mall.pms.dto.PmsStockInDetailQueryDto;
import com.yz.mall.pms.dto.PmsStockInDetailUpdateDto;
import com.yz.mall.pms.entity.PmsStockInDetail;
import com.yz.mall.pms.mapper.PmsStockInDetailMapper;
import com.yz.mall.pms.service.PmsStockInDetailService;
import com.yz.mall.web.common.PageFilter;
import com.yz.unqid.service.InternalUnqidService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * 产品管理-商品入库日志表(PmsStockInDetail)表服务实现类
 *
 * @author yunze
 * @since 2024-12-25 19:53:27
 */
@Service
public class PmsStockInDetailServiceImpl extends ServiceImpl<PmsStockInDetailMapper, PmsStockInDetail> implements PmsStockInDetailService {

    private final InternalUnqidService internalUnqidService;

    public PmsStockInDetailServiceImpl(InternalUnqidService internalUnqidService) {
        this.internalUnqidService = internalUnqidService;
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
    public Page<PmsStockInDetail> page(PageFilter<PmsStockInDetailQueryDto> filter) {
        PmsStockInDetailQueryDto queryFilter = filter.getFilter();
        LambdaQueryWrapper<PmsStockInDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(queryFilter.getStockInCode()), PmsStockInDetail::getStockInCode, queryFilter.getStockInCode());
        queryWrapper.eq(queryFilter.getProductId() != null, PmsStockInDetail::getProductId, queryFilter.getProductId());
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

