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
import com.yz.mall.pms.mapper.PmsStockOutDetailMapper;
import com.yz.mall.pms.entity.PmsStockOutDetail;
import com.yz.mall.pms.service.PmsStockOutDetailService;
import com.yz.mall.web.common.PageFilter;
import com.yz.unqid.service.InternalUnqidService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品管理-商品出库日志表(PmsStockOutDetail)表服务实现类
 *
 * @author yunze
 * @since 2024-12-27 12:50:27
 */
@Service
public class PmsStockOutDetailServiceImpl extends ServiceImpl<PmsStockOutDetailMapper, PmsStockOutDetail> implements PmsStockOutDetailService {

    private final InternalUnqidService internalUnqidService;

    public PmsStockOutDetailServiceImpl(InternalUnqidService internalUnqidService) {
        this.internalUnqidService = internalUnqidService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long out(PmsStockOutDetailAddDto dto) {
        PmsStockOutDetail bo = new PmsStockOutDetail();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        String prefix = "CK" + LocalDateTimeUtil.format(LocalDate.now(), DatePattern.PURE_DATE_PATTERN);
        bo.setStockOutCode(internalUnqidService.generateSerialNumber(prefix, 6));
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean outBatch(List<InternalPmsStockDto> outDetails) {
        String prefix = "CK" + LocalDateTimeUtil.format(LocalDate.now(), DatePattern.PURE_DATE_PATTERN);
        String stockOutCode = internalUnqidService.generateSerialNumber(prefix, 6);
        List<PmsStockOutDetail> outDetailList = new ArrayList<>();
        outDetails.forEach(out -> {
            PmsStockOutDetail bo = new PmsStockOutDetail();
            bo.setId(IdUtil.getSnowflakeNextId());
            bo.setStockOutCode(stockOutCode);
            bo.setProductId(out.getProductId());
            bo.setQuantity(out.getQuantity());
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
    public Page<PmsStockOutDetail> page(PageFilter<PmsStockOutDetailQueryDto> filter) {
        LambdaQueryWrapper<PmsStockOutDetail> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

