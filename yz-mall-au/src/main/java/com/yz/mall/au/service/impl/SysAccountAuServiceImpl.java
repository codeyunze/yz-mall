package com.yz.mall.au.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.au.dto.SysAccountAuAddDto;
import com.yz.mall.au.dto.SysAccountAuChooseQueryDto;
import com.yz.mall.au.dto.SysAccountAuQueryDto;
import com.yz.mall.au.dto.SysAccountAuUpdateDto;
import com.yz.mall.au.mapper.SysAccountAuMapper;
import com.yz.mall.au.entity.SysAccountAu;
import com.yz.mall.au.service.SysAccountAuService;
import com.yz.mall.au.vo.SysAccountAuChooseVo;
import com.yz.mall.au.vo.SysAccountAuVo;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 个人黄金账户(SysAccountAu)表服务实现类
 *
 * @author yunze
 * @since 2025-01-05 10:06:32
 */
@Service
public class SysAccountAuServiceImpl extends ServiceImpl<SysAccountAuMapper, SysAccountAu> implements SysAccountAuService {

    @Override
    public Long save(SysAccountAuAddDto dto) {
        SysAccountAu bo = new SysAccountAu();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setUserId(Long.parseLong(StpUtil.getLoginId().toString()));
        if (1 == dto.getTransactionType()) {
            // 计算盈利金额
            SysAccountAu purchase = baseMapper.selectById(dto.getRelationId());
            // 卖出价格 - 买入价格 - 2.5元手续费
            bo.setProfitAmount(bo.getPrice().subtract(purchase.getPrice()).subtract(BigDecimal.valueOf(2.5)).multiply(BigDecimal.valueOf(bo.getQuantity())));
        }
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysAccountAuUpdateDto dto) {
        SysAccountAu au = baseMapper.selectById(dto.getId());

        au.setRelationId(dto.getRelationId());
        au.setPrice(dto.getPrice());
        au.setQuantity(dto.getQuantity());

        if (!au.getTransactionType().equals(dto.getTransactionType())) {
            if (0 == au.getTransactionType()) {
                // 更新前是买入记录，更新后是卖出记录，需要调整关联这一条买入记录的卖出记录
                LambdaQueryWrapper<SysAccountAu> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SysAccountAu::getRelationId, au.getId());
                // 卖出记录
                List<SysAccountAu> list = baseMapper.selectList(queryWrapper);
                list.forEach(item -> {
                    item.setProfitAmount(BigDecimal.ZERO);
                    item.setRelationId(-1L);
                });
                // 清理关联到当前更新交易记录的卖出记录
                super.updateBatchById(list);

                // 计算盈利金额
                SysAccountAu purchase = baseMapper.selectById(dto.getRelationId());
                // 卖出价格 - 买入价格 - 2.5元手续费
                au.setProfitAmount(dto.getPrice().subtract(purchase.getPrice()).subtract(BigDecimal.valueOf(2.5)).multiply(BigDecimal.valueOf(au.getQuantity())));
            } else {
                // 更新前是卖出记录，更新后是买入记录
                au.setProfitAmount(BigDecimal.ZERO);
                au.setRelationId(-1L);
            }
            au.setTransactionType(dto.getTransactionType());
        } else {
            if (0 == au.getTransactionType()) {
                // 更新前是买入记录，需要调整关联这个买入记录的所有卖出记录的"盈利金额"
                LambdaQueryWrapper<SysAccountAu> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SysAccountAu::getTransactionType, 1).eq(SysAccountAu::getRelationId, au.getId());
                // 卖出记录
                List<SysAccountAu> list = baseMapper.selectList(queryWrapper);
                list.forEach(item -> {
                    item.setProfitAmount(item.getPrice().subtract(au.getPrice()).subtract(BigDecimal.valueOf(2.5)).multiply(BigDecimal.valueOf(item.getQuantity())));
                });
                super.updateBatchById(list);
            } else {
                // 更新前是卖出记录，需要调整本记录的"盈利金额"
                // 计算盈利金额
                SysAccountAu purchase = baseMapper.selectById(dto.getRelationId());
                // 卖出价格 - 买入价格 - 2.5元手续费
                au.setProfitAmount(dto.getPrice().subtract(purchase.getPrice()).subtract(BigDecimal.valueOf(2.5)).multiply(BigDecimal.valueOf(au.getQuantity())));
            }
        }
        return baseMapper.updateById(au) > 0;
    }

    @DS("slave")
    @Override
    public Page<SysAccountAuVo> getPageByFilter(PageFilter<SysAccountAuQueryDto> filter) {
        return baseMapper.selectPageByFilter(new Page<>(filter.getCurrent(), filter.getSize()), filter.getFilter());
    }

    @DS("slave")
    @Override
    public Page<SysAccountAuVo> getPageSummaryByFilter(PageFilter<SysAccountAuQueryDto> filter) {
        // 查询出买入记录信息
        filter.getFilter().setTransactionType(0);
        Page<SysAccountAuVo> voPage = baseMapper.selectPageByFilter(new Page<>(filter.getCurrent(), filter.getSize()), filter.getFilter());
        if (voPage.getTotal() == 0) {
            return voPage;
        }

        // 查询出这几页买入记录信息的所有卖出记录信息
        List<Long> purchaseIds = voPage.getRecords().stream().map(SysAccountAuVo::getId).collect(Collectors.toList());
        LambdaQueryWrapper<SysAccountAu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysAccountAu::getRelationId, purchaseIds);
        queryWrapper.eq(SysAccountAu::getTransactionType, 1);
        queryWrapper.orderByDesc(SysAccountAu::getId);
        List<SysAccountAu> sellOutRecords = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(sellOutRecords)) {
            return voPage;
        }

        // 统计这些买入记录的收益信息
        Map<Long, List<SysAccountAuVo>> sellOutByPurchaseMap = sellOutRecords.stream().map(t -> {
            SysAccountAuVo vo = new SysAccountAuVo();
            BeanUtils.copyProperties(t, vo);
            return vo;
        }).collect(Collectors.groupingBy(SysAccountAuVo::getRelationId));
        voPage.getRecords().forEach(purchase -> {
            purchase.setSellOutRecords(sellOutByPurchaseMap.get(purchase.getId()));
            purchase.setProposalPrice(purchase.getPrice().add(BigDecimal.valueOf(2.5)));
            purchase.setSurplusQuantity(purchase.getQuantity());
            if (!CollectionUtils.isEmpty(purchase.getSellOutRecords())) {
                purchase.getSellOutRecords().forEach(sellOut -> {
                    // 计算剩余克数
                    purchase.setSurplusQuantity(purchase.getSurplusQuantity() - sellOut.getQuantity());
                    // 计算盈利金额 [(卖出单价-买入单价-2.5) * 卖出数量]
                    BigDecimal profitAmount = sellOut.getPrice().subtract(purchase.getPrice()).subtract(BigDecimal.valueOf(2.5)).multiply(BigDecimal.valueOf(sellOut.getQuantity()));
                    purchase.setProfitAmount(purchase.getProfitAmount().add(profitAmount));
                });
            }
        });

        return voPage;
    }

    @Override
    public Page<SysAccountAuChooseVo> getChooseByFilter(PageFilter<SysAccountAuChooseQueryDto> filter) {
        filter.getFilter().setPrice(filter.getFilter().getPrice().subtract(BigDecimal.valueOf(2.5)));
        // 查询出买入记录信息
        Page<SysAccountAuChooseVo> voPage = baseMapper.selectChoosePageByFilter(new Page<>(filter.getCurrent(), filter.getSize()), filter.getFilter());
        if (voPage.getTotal() == 0) {
            return voPage;
        }
        voPage.getRecords().forEach(purchase -> {
           purchase.setProposalPrice(purchase.getPrice().add(BigDecimal.valueOf(2.5)));
        });
        return voPage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Long id) {
        SysAccountAu record = baseMapper.selectById(id);
        if (1 == record.getTransactionType()) {
            // 清理卖出记录
            return baseMapper.deleteById(id) > 0;
        }

        // 调整关联这一条买入记录的卖出记录
        LambdaQueryWrapper<SysAccountAu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysAccountAu::getRelationId, id);
        // 卖出记录
        List<SysAccountAu> list = baseMapper.selectList(queryWrapper);
        list.forEach(item -> {
            item.setProfitAmount(BigDecimal.ZERO);
            item.setRelationId(-1L);
        });
        super.updateBatchById(list);
        return baseMapper.deleteById(id) > 0;
    }
}

